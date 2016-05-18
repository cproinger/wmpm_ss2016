package at.ac.tuwien.wmpm.service;

import at.ac.tuwien.wmpm.ss2016.VoteInfo;
import at.ac.tuwien.wmpm.ss2016.VoteInfo.Item;

public interface ExtractCandidateVoteService {

	String ping();

	/**
	 * 
	 * @param voteInfo
	 * 		the item-list of this object must not be null
	 * @return
	 * @throws IllegalVoteException
	 */
	Item extract(VoteInfo voteInfo) throws IllegalVoteException;
}
