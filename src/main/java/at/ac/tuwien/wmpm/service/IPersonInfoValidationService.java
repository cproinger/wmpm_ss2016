package at.ac.tuwien.wmpm.service;

import at.ac.tuwien.wmpm.domain.Person;
import at.ac.tuwien.wmpm.ss2016.VoteRequest;

public interface IPersonInfoValidationService {

  /**
   *
   * @param voteRequest, the vote request, for which to validate personal info.
   * @return the related person object, if valid info is given.
   * @throws IllegalPersonInfoException if person is not registered or voting card is not registered.
   */
  Person validate(VoteRequest voteRequest) throws IllegalPersonInfoException;

}
