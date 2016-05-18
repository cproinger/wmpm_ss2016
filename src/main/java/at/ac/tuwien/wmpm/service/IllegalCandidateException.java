package at.ac.tuwien.wmpm.service;

import at.ac.tuwien.wmpm.ss2016.VoteInfo.Item;

public class IllegalCandidateException extends IllegalVoteInfoException {

	private Item voteItem;

	public IllegalCandidateException(Item item) {
		this.voteItem = item;
	}

	private static final long serialVersionUID = 1L;

	
}
