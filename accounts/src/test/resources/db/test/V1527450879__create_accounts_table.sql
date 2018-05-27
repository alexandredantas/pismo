create table accounts.accounts
(
	id INTEGER IDENTITY PRIMARY KEY,
	credit_limit numeric(20,2) not null,
	withdrawal_limit numeric(20,2) not null
)
;

