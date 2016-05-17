CREATE TABLE Product (
  id BIGINT IDENTITY,
  name varchar(255) not null unique
);


CREATE TABLE polls (
  id BIGINT IDENTITY,
  candidate  VARCHAR(255) NOT NULL,
  vote_count INTEGER           NOT NULL
);

create TABLE candidate (
	name VARCHAR(255) PRIMARY KEY
);