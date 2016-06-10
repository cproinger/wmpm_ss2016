CREATE TABLE Product (
  id BIGINT IDENTITY,
  name VARCHAR (255) NOT NULL UNIQUE
);

CREATE TABLE polls (
  id BIGINT IDENTITY,
  candidate  VARCHAR(255) NOT NULL,
  vote_count INTEGER NOT NULL
);


CREATE TABLE candidate (
	name VARCHAR(255) PRIMARY KEY
);

CREATE TABLE voting_card (
  id VARCHAR(255) PRIMARY KEY
);

CREATE TABLE person (
  id VARCHAR(255) PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  voted BOOLEAN DEFAULT false,
  voting_card_id VARCHAR(255),
  FOREIGN KEY (voting_card_id) REFERENCES voting_card(id)
);

CREATE TABLE resultDataToPublis (
  id BIGINT IDENTITY,
  product_name VARCHAR(255) NOT NULL,
  candidate_name  VARCHAR(255) NOT NULL,
  comment varchar(500)
);