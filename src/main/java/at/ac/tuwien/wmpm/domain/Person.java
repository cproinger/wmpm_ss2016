package at.ac.tuwien.wmpm.domain;

import javax.persistence.*;

@Entity
@Table(name = "person")
public class Person {

  @Id
  private String id;

  @Column(name = "name")
  private String name;

  @Column(name = "voted")
  private boolean voted;

  @OneToOne
  @JoinColumn(name = "voting_card_id")
  private VotingCard votingCard;

  public boolean hasVoted() {
    return this.voted;
  }

  public String getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public VotingCard getVotingCard() {
    return this.votingCard;
  }
}
