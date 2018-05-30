CREATE TABLE transactions.payments_tracking
(
    id integer IDENTITY PRIMARY KEY NOT NULL,
    credit_transaction_id bigint NOT NULL,
    debit_transaction_id bigint NOT NULL,
    amount numeric(20,4) NOT NULL
);