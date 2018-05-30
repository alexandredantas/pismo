package com.pismo.transactions.data.repositories;

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pismo.transactions.data.entities.Transactions;

public interface TransactionsRepository extends JpaRepository<Transactions, Long> {

  default Optional<Transactions> saveAndFlushOpt(Transactions entity) {
    try{
      return Optional.of(this.saveAndFlush(entity));
    } catch(Exception e){
      return Optional.empty();
    }
  }

  @Query(value = "select t from Transactions t where t.balance < 0 and t.operationType != 4 and t.accountId = ?1 order by t.operationType, t.eventDate")
  Stream<Transactions> findExistingDebts(Long accountId);
}
