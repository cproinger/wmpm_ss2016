package at.ac.tuwien.wmpm.service;

import at.ac.tuwien.wmpm.domain.Candidate;
import at.ac.tuwien.wmpm.ss2016.VoteInfo;

public interface ExtractCandidateVoteService {

	String ping();

	/**
	 * 
	 * @param voteInfo
	 * 		the item-list of this object must not be null
	 * @return
	 * @throws IllegalVoteException
	 */
	Candidate extract(VoteInfo voteInfo) throws IllegalVoteException;
}
