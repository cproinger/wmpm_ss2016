package at.ac.tuwien.wmpm.domain;

import org.springframework.data.annotation.Id;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "voting_card")
public class VotingCard {

  @Id
  private String id;
}
