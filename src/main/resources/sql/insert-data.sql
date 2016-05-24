INSERT INTO Product(name) values('Product 1');
INSERT INTO Product(name) values('Product 2');
INSERT INTO Product(name) values('Product 3');
INSERT INTO Product(name) values('Product 4');
INSERT INTO Product(name) values('Product 5');
INSERT INTO Product(name) values('Product 6');
INSERT INTO Product(name) values('Product 7');
INSERT INTO Product(name) values('Product 8');
INSERT INTO Product(name) values('Product 9');

INSERT INTO candidate(name) VALUES('Beavis');
INSERT INTO candidate(name) VALUES('Butthead');

INSERT INTO resultDataToPublis(product_name, candidate_name, comment) values('Product 1', 'Beavis'  , 'The final result of voting : ' );
INSERT INTO resultDataToPublis(product_name, candidate_name, comment) values('Product 2', 'Butthead', 'Voting results:  ' );

INSERT INTO voting_card(id) VALUES('df85485fe016bd220f6d87485dbab7c3c4bfc243');
INSERT INTO voting_card(id) VALUES('a94a8fe5ccb19ba61c4c0873d391e987982fbbd3');

INSERT INTO person(id, name, voting_card_id) VALUES('64abaade7c05afb815c615d3c43509fbd6ebd2ab', 'John Doe', 'df85485fe016bd220f6d87485dbab7c3c4bfc243');
INSERT INTO person(id, name, voting_card_id) VALUES('3a9409f476bd25339c65e3227db40ab34629f9be', 'Homer Simpson', 'a94a8fe5ccb19ba61c4c0873d391e987982fbbd3');