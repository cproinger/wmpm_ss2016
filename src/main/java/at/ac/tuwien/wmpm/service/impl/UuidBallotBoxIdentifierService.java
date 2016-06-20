package at.ac.tuwien.wmpm.service.impl;

import at.ac.tuwien.wmpm.service.BallotBoxIdentifierService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("ballotBoxIdentifierService")
public class UuidBallotBoxIdentifierService implements BallotBoxIdentifierService {
    private static final UUID ID = UUID.randomUUID();

    @Override public String getUniqueBallotBoxId() {
        return ID.toString();
    }
}
