CREATE TABLE transactions.transactions
(
    id bigserial PRIMARY KEY NOT NULL,
    account_id bigint NOT NULL,
    operation_type int NOT NULL,
    amount numeric(20,4) NOT NULL,
    balance numeric(20,4) NOT NULL,
    event_date timestamp with time zone NOT NULL,
    due_date timestamp with time zone NOT NULL
);

CREATE INDEX transactions_debt_order_index ON transactions.transactions (operation_type, event_date);

CREATE INDEX transactions_debt_balance_index ON transactions.transactions (balance);