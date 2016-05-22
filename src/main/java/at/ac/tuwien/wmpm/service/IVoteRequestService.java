package at.ac.tuwien.wmpm.service;

import at.ac.tuwien.wmpm.ss2016.VoteRequest;
import at.ac.tuwien.wmpm.ss2016.VoteResponse;

import javax.xml.bind.JAXBElement;

public interface IVoteRequestService {

  /**
   *
   * @param voteRequest, the vote request, for which to handleRequest personal info.
   * @return a VoteResponse
   */
  JAXBElement<VoteResponse> handleRequest(VoteRequest voteRequest);

}
