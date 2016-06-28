package at.ac.tuwien.wmpm.service;

import at.ac.tuwien.wmpm.helper.TransformedRequest;
import at.ac.tuwien.wmpm.ss2016.VoteRequest;

public interface VoteRequestService {

  /**
   * Checks if the ballot is still open.
   * @throws BallotClosedException if ballot is already closed.
   */
  void checkIfBallotIsOpen() throws BallotClosedException;

  /**
   *
   * @param voteRequest, the vote request, for which to handleRequest personal info.
   * @throws IllegalPersonInfoException if voting card info or personal id are wrong.
   * @throws AlreadyVotedException if person has already voted.
   */
  VoteRequest doVote(TransformedRequest voteRequest) throws IllegalPersonInfoException, AlreadyVotedException;

  /**
   * Checks if the given person id is valid (i.e. the person exists in the
   * database).
   *
   * @param personId, the id to check.
   * @throws IllegalPersonInfoException if no person with the given id exists.
   */
  void validatePersonalId(String personId) throws IllegalPersonInfoException;

  /**
   * Checks if the given voting card is valid.
   *
   * @param votingCardId, the voting card id to check.
   * @throws IllegalPersonInfoException
   */
  void validateVotingCardId(String votingCardId) throws IllegalPersonInfoException;

  /**
   *
   * @param voteRequest, the vote request.
   */
  TransformedRequest transformRequest(VoteRequest voteRequest);

}
