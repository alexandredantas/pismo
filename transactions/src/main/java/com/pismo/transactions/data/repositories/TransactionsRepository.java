package com.pismo.transactions.data.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pismo.transactions.data.entities.Transactions;

public interface TransactionsRepository extends JpaRepository<Transactions, Long> {

  default <S extends Transactions> Optional<S> saveAndFlushOpt(S entity) {
    try{
      return Optional.of(this.saveAndFlush(entity));
    } catch(Exception e){
      return Optional.empty();
    }
  }
}
