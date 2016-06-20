package at.ac.tuwien.wmpm.service;

public interface BallotBoxIdentifierService {
    /**
     * @return an identifier for a ballot box which is unique among all ballot boxes.
     */
    String getUniqueBallotBoxId();
}
