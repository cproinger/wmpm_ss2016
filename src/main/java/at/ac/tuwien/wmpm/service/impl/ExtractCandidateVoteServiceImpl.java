package at.ac.tuwien.wmpm.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import at.ac.tuwien.wmpm.service.ExtractCandidateVoteService;
import at.ac.tuwien.wmpm.service.IllegalVoteException;
import org.springframework.stereotype.Service;

import at.ac.tuwien.wmpm.ss2016.VoteInfo;
import at.ac.tuwien.wmpm.ss2016.VoteInfo.Item;

@Service("extractCandidateVoteService")
public class ExtractCandidateVoteServiceImpl implements ExtractCandidateVoteService {

	@Override
	public Item extract(VoteInfo voteInfo) throws IllegalVoteException {
		
		
		List<Item> validItems = voteInfo.getItem().stream()
			.filter(c -> c.getMark() != null && c.getMark().length() > 0)
			.collect(Collectors.toList());

		if(validItems.size() != 1) {
			throw new IllegalVoteException(voteInfo);
		}
		
		return validItems.get(0);
	}
}
