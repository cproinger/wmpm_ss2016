package at.ac.tuwien.wmpm.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Service;

import at.ac.tuwien.wmpm.domain.Candidate;
import at.ac.tuwien.wmpm.ss2016.VoteInfo;

@Service("extractCandidateVoteService")
public class ExtractCandidateVoteServiceImpl implements ExtractCandidateVoteService {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public Candidate extract(VoteInfo voteInfo) throws IllegalVoteException {
		
		TypedQuery<Candidate> q = entityManager.createQuery("select c from Candidate c where c.name like :name"
				, Candidate.class);
		
		List<Candidate> candidateVotes = voteInfo.getItem().stream()
			.map(i -> q.setParameter("name", i.getCandiate()).getResultList().stream().findFirst())
			.filter(c -> c.isPresent())
			.map(c -> c.get())
			.collect(Collectors.toList());
		
		if(candidateVotes.size() != 1) {
			throw new IllegalVoteException(voteInfo);
		}
		
		return candidateVotes.get(0);
	}
	
	@Override
	public String ping() {
		return "pong";
	}
}
