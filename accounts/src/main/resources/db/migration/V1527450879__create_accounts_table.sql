create table accounts.accounts
(
	id bigserial not null constraint accounts_pkey primary key,
	credit_limit numeric(20,2) not null,
	withdrawal_limit numeric(20,2) not null
)
;

