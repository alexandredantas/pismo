package com.pismo.transactions.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pismo.transactions.data.entities.Transactions;

public interface TransactionsRepository extends JpaRepository<Transactions, Long> {
}
