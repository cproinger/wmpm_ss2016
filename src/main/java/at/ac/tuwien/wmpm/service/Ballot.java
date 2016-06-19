package at.ac.tuwien.wmpm.service;

import org.springframework.stereotype.Service;

@Service("ballot")
public class Ballot {

	private boolean closed;

	public void close() {
		this.closed = true;
	}

	public boolean isOpen() {
		return !closed;
	}
}
