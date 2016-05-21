package at.ac.tuwien.wmpm.service.impl;

import at.ac.tuwien.wmpm.domain.Person;
import at.ac.tuwien.wmpm.service.IPersonInfoValidationService;
import at.ac.tuwien.wmpm.service.IllegalPersonInfoException;
import at.ac.tuwien.wmpm.ss2016.VoteRequest;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Service("personInfoValidationService")
public class PersonInfoValidationService implements IPersonInfoValidationService {

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public Person validate(VoteRequest voteRequest) throws IllegalPersonInfoException {

    try {

      System.out.println("MESSAGE RECEIVED WITH: " + voteRequest.getPersonInfo().getPersonalIdentificationDoc());
      return entityManager.createQuery("select p from Person p where p.id = :person_id and p.votingCard.id = :voting_card_id", Person.class)
              .setParameter("person_id", voteRequest.getPersonInfo().getPersonalIdentificationDoc())
              .setParameter("voting_card_id", voteRequest.getPersonInfo().getVotingCard())
              .getSingleResult();
    }
    catch (NoResultException e) {
      throw new IllegalPersonInfoException();
    }
  }
}
