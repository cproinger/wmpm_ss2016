package at.ac.tuwien.wmpm.repository;

import java.math.BigInteger;

import org.springframework.data.mongodb.repository.MongoRepository;

import at.ac.tuwien.wmpm.domain.Vote;
import at.ac.tuwien.wmpm.ss2016.VoteInfo;
import at.ac.tuwien.wmpm.ss2016.VoteRequest;

public interface VoteRepository extends MongoRepository<Vote, BigInteger> {

}
