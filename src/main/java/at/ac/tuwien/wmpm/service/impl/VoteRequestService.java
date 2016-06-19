package at.ac.tuwien.wmpm.service.impl;

import at.ac.tuwien.wmpm.domain.Person;
import at.ac.tuwien.wmpm.domain.VotingCard;
import at.ac.tuwien.wmpm.helper.TransformedRequest;
import at.ac.tuwien.wmpm.helper.ValidationObject;
import at.ac.tuwien.wmpm.helper.ValidationType;
import at.ac.tuwien.wmpm.service.AlreadyVotedException;
import at.ac.tuwien.wmpm.service.IVoteRequestService;
import at.ac.tuwien.wmpm.service.IllegalPersonInfoException;
import at.ac.tuwien.wmpm.ss2016.VoteRequest;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service("voteRequestService")
public class VoteRequestService implements IVoteRequestService {

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public VoteRequest doVote(TransformedRequest voteRequest) throws IllegalPersonInfoException, AlreadyVotedException {

    VoteRequest request = voteRequest.getRequest();

    try {
      Person p = entityManager.createQuery("select p from Person p where p.id = :person_id and p.votingCard.id = :voting_card_id", Person.class)
              .setParameter("person_id", request.getPersonInfo().getPersonalIdentificationDoc())
              .setParameter("voting_card_id", request.getPersonInfo().getVotingCard())
              .getSingleResult();

      if(p.hasVoted()) {
        throw new AlreadyVotedException();
      }
      else {
        p.setVoted(true);
        entityManager.merge(p);
      }
    }
    catch (NoResultException e) {
      throw new IllegalPersonInfoException();
    }
    return request;
  }

  @Override
  public void validatePersonalId(String personId) throws IllegalPersonInfoException {
    try {
      entityManager.createQuery("select p from Person p where p.id = :person_id", Person.class)
              .setParameter("person_id", personId)
              .getSingleResult();

    }
    catch (NoResultException e) {
      throw new IllegalPersonInfoException();
    }
  }

  @Override
  public void validateVotingCardId(String votingCardId) throws IllegalPersonInfoException {
    try {
      entityManager.createQuery("select v from VotingCard v where v.id = :id", VotingCard.class)
              .setParameter("id", votingCardId)
              .getSingleResult();

    }
    catch (NoResultException e) {
      throw new IllegalPersonInfoException();
    }
  }

  @Override
  public TransformedRequest transformRequest(VoteRequest voteRequest) {

    TransformedRequest result = new TransformedRequest();
    List<ValidationObject> list = new ArrayList<>();

    list.add(new ValidationObject(ValidationType.PERSONAL_INFORMATION_ID, voteRequest.getPersonInfo().getPersonalIdentificationDoc()));
    list.add(new ValidationObject(ValidationType.VOTING_CARD_ID, voteRequest.getPersonInfo().getVotingCard()));

    result.setValidationObjectList(list);
    result.setRequest(voteRequest);

    return result;
  }
}
