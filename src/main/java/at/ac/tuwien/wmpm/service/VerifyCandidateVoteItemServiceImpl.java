package at.ac.tuwien.wmpm.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Service;

import at.ac.tuwien.wmpm.domain.Candidate;
import at.ac.tuwien.wmpm.ss2016.VoteInfo;

@Service("verifyCandidateVoteItemService")
public class VerifyCandidateVoteItemServiceImpl implements VerifyCandidateVoteItemService {


	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public Candidate lookupCandidate(VoteInfo.Item item) throws IllegalCandidateException {
		TypedQuery<Candidate> q = entityManager.createQuery("select c from Candidate c where c.name like :name"
				, Candidate.class);
		
		List<Candidate> candidateVotes = q.setParameter("name", item.getCandiate()).getResultList();
		
		if(candidateVotes.size() != 1) {
			throw new IllegalCandidateException(item);
		}
		
		return candidateVotes.get(0);
	}
}
