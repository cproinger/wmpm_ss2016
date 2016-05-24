package at.ac.tuwien.wmpm.service.impl;

import at.ac.tuwien.wmpm.ss2016.ObjectFactory;
import at.ac.tuwien.wmpm.ss2016.ResponseType;
import at.ac.tuwien.wmpm.ss2016.VoteResponse;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBElement;

@Service("voteResponseFactory")
public class VoteResponseFactory {

  public JAXBElement<VoteResponse> createPersonErrorResponse() {
    return this.createVoteResponseFromType(ResponseType.PERSON_INFO_ERROR);
  }

  public JAXBElement<VoteResponse> createAlreadyVotedResponse() {
    return this.createVoteResponseFromType(ResponseType.ALREADY_VOTED);
  }

  public JAXBElement<VoteResponse> createValidResponse() {
    return this.createVoteResponseFromType(ResponseType.OK);
  }

  private JAXBElement<VoteResponse> createVoteResponseFromType(ResponseType type) {

    ObjectFactory factory = new ObjectFactory();
    VoteResponse response = new VoteResponse();
    response.setStatus(type);
    return factory.createVoteResponse(response);
  }
}
