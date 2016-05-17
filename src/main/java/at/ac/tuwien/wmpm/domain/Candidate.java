package at.ac.tuwien.wmpm.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="candidate")
public class Candidate {

	@Id
	private String name;
	
	public String getName() {
		return name;
	}
}
