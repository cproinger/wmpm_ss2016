package at.ac.tuwien.wmpm.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import at.ac.tuwien.wmpm.Integration;

@Entity
@XmlType
@XmlRootElement
public class Product {

	@Id
	@GeneratedValue
	private Long id;
	
	@NotNull
	@Column(unique = true)
	private String name;
	
	
	public Long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@PostLoad
	private void postLoad() {
		Integration.get().loaded(id);
	}
}
