package at.ac.tuwien.wmpm.service;

import at.ac.tuwien.wmpm.ss2016.VoteRequest;

public interface IVoteRequestService {

  /**
   *
   * @param voteRequest, the vote request, for which to handleRequest personal info.
   * @throws IllegalPersonInfoException if voting card info or personal id are wrong.
   * @throws AlreadyVotedException if person has already voted.
   */
  VoteRequest handleRequest(VoteRequest voteRequest) throws IllegalPersonInfoException, AlreadyVotedException;

}
