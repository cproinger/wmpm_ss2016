package at.ac.tuwien.wmpm.service;

import at.ac.tuwien.wmpm.ss2016.VoteInfo;

public class IllegalVoteException extends IllegalVoteInfoException {

	private static final long serialVersionUID = 1L;
	private VoteInfo voteInfo;

	public IllegalVoteException(VoteInfo voteInfo) {
		this.voteInfo = voteInfo;
	}

}
