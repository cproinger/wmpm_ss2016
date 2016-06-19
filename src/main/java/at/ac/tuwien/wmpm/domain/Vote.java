package at.ac.tuwien.wmpm.domain;

import java.math.BigInteger;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import at.ac.tuwien.wmpm.ss2016.VoteInfo;

@Document(collection = "votes")
public class Vote {

	@Id
	private BigInteger id;
	
	private VoteInfo voteInfo;
	
	public Vote() {
		//empty
	}
	
	public Vote(VoteInfo voteInfo) {
		this.voteInfo = voteInfo;
	}
	
	public VoteInfo getVoteInfo() {
		return voteInfo;
	}

	@Override
	public String toString() {
		return "Vote [id=" + id + ", voteInfo=" + voteInfo + "]";
	}
	
	
}
