package at.ac.tuwien.wmpm.service;

import org.springframework.stereotype.Service;

@Service("extractCandidateVoteService")
public class ExtractCandidateVoteServiceImpl implements ExtractCandidateVoteService {

	@Override
	public String ping() {
		return "pong";
	}
}
