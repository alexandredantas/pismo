CREATE TABLE transactions.operationtypes
(
    id int PRIMARY KEY NOT NULL,
    description varchar(50) NOT NULL,
    charge_order int NOT NULL
);

insert into transactions.operationtypes(id, description, charge_order) values (1, 'COMPRA A VISTA', 2);
insert into transactions.operationtypes(id, description, charge_order) values (2, 'COMPRA PARCELADA', 1);
insert into transactions.operationtypes(id, description, charge_order) values (3, 'SAQUE', 0);
insert into transactions.operationtypes(id, description, charge_order) values (4, 'PAGAMENTO', 0);