package at.ac.tuwien.wmpm.service.impl;

import at.ac.tuwien.wmpm.domain.Person;
import at.ac.tuwien.wmpm.service.IVoteRequestService;
import at.ac.tuwien.wmpm.ss2016.ObjectFactory;
import at.ac.tuwien.wmpm.ss2016.ResponseType;
import at.ac.tuwien.wmpm.ss2016.VoteRequest;
import at.ac.tuwien.wmpm.ss2016.VoteResponse;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.xml.bind.JAXBElement;

@Transactional
@Service("voteRequestService")
public class VoteRequestService implements IVoteRequestService {

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public JAXBElement<VoteResponse> handleRequest(VoteRequest voteRequest) {

    ObjectFactory factory = new ObjectFactory();
    VoteResponse response = new VoteResponse();
    response.setStatus(ResponseType.OK);

    try {
      Person p = entityManager.createQuery("select p from Person p where p.id = :person_id and p.votingCard.id = :voting_card_id", Person.class)
              .setParameter("person_id", voteRequest.getPersonInfo().getPersonalIdentificationDoc())
              .setParameter("voting_card_id", voteRequest.getPersonInfo().getVotingCard())
              .getSingleResult();

      if(p.hasVoted()) {
        response.setStatus(ResponseType.ALREADY_VOTED);
      }
      else {
        p.setVoted(true);
        entityManager.merge(p);
      }
    }
    catch (NoResultException e) {
      response.setStatus(ResponseType.PERSON_INFO_ERROR);
    }
    return factory.createVoteResponse(response);
  }
}
