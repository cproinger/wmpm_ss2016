package at.ac.tuwien.wmpm.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "voting_card")
public class VotingCard {

  @Id
  private String id;

  public String getId() {
    return id;
  }
}
