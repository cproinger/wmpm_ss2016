package at.ac.tuwien.wmpm.service;

import at.ac.tuwien.wmpm.domain.Candidate;
import at.ac.tuwien.wmpm.ss2016.VoteInfo.Item;

public interface VerifyCandidateVoteItemService {

	Candidate lookupCandidate(Item item) throws IllegalCandidateException;

}
