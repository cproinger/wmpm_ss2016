package at.ac.tuwien.wmpm.service.impl;

import at.ac.tuwien.wmpm.domain.Person;
import at.ac.tuwien.wmpm.service.AlreadyVotedException;
import at.ac.tuwien.wmpm.service.IVoteRequestService;
import at.ac.tuwien.wmpm.service.IllegalPersonInfoException;
import at.ac.tuwien.wmpm.ss2016.VoteRequest;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Transactional
@Service("voteRequestService")
public class VoteRequestService implements IVoteRequestService {

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public VoteRequest handleRequest(VoteRequest voteRequest) throws IllegalPersonInfoException, AlreadyVotedException {

    try {
      Person p = entityManager.createQuery("select p from Person p where p.id = :person_id and p.votingCard.id = :voting_card_id", Person.class)
              .setParameter("person_id", voteRequest.getPersonInfo().getPersonalIdentificationDoc())
              .setParameter("voting_card_id", voteRequest.getPersonInfo().getVotingCard())
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
    return voteRequest;
  }
}
