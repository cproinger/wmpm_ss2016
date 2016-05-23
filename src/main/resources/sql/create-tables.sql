CREATE TABLE Product (
  id BIGINT IDENTITY,
  name VARCHAR(255) NOT NULL UNIQUE
);


CREATE TABLE polls (
  id BIGINT IDENTITY,
  candidate  VARCHAR(255) NOT NULL,
  vote_count INTEGER NOT NULL
);


CREATE TABLE candidate (
	name VARCHAR(255) PRIMARY KEY
);


CREATE TABLE resultDataToPublis (
  id BIGINT IDENTITY,
  product_name VARCHAR(255) NOT NULL,
  candidate_name  VARCHAR(255) NOT NULL,
  comment varchar(500)
);