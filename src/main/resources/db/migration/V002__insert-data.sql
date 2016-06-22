INSERT INTO candidate(name) VALUES('Beavis');
INSERT INTO candidate(name) VALUES('Butthead');
INSERT INTO candidate(name) VALUES('Trump');
INSERT INTO candidate(name) VALUES('Lugner');

-- INSERT INTO resultDataToPublis(product_name, candidate_name, comment) values('Product 1', 'Beavis'  , 'The final result of voting : ' );
-- INSERT INTO resultDataToPublis(product_name, candidate_name, comment) values('Product 2', 'Butthead', 'Voting results:  ' );

insert into numbers values(0);
insert into numbers values(1);
insert into numbers values(2);
insert into numbers values(3);
insert into numbers values(4);
insert into numbers values(5);
insert into numbers values(6);
insert into numbers values(7);
insert into numbers values(8);
insert into numbers values(9);

-- create 10.000 voting cards
insert into voting_card(id)
	select CAST((n1.n * 1 + n2.n * 10 + n3.n * 100 + n4.n * 1000) AS VARCHAR)
	from numbers n1 
		CROSS JOIN numbers n2
		CROSS JOIN numbers n3
		CROSS JOIN numbers n4;
		
insert into person(id, name, voting_card_id)
	select id, CONCAT('Person ', id), id
	from voting_card;

INSERT INTO voting_card(id) VALUES('df85485fe016bd220f6d87485dbab7c3c4bfc243');
INSERT INTO voting_card(id) VALUES('a94a8fe5ccb19ba61c4c0873d391e987982fbbd3');

INSERT INTO person(id, name, voting_card_id) VALUES('64abaade7c05afb815c615d3c43509fbd6ebd2ab', 'John Doe', 'df85485fe016bd220f6d87485dbab7c3c4bfc243');
INSERT INTO person(id, name, voting_card_id) VALUES('3a9409f476bd25339c65e3227db40ab34629f9be', 'Homer Simpson', 'a94a8fe5ccb19ba61c4c0873d391e987982fbbd3');