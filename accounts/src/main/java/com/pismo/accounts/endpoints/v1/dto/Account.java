package com.pismo.accounts.endpoints.v1.dto;

import java.math.BigDecimal;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Account {

  public static class Limit {

    private static final Limit ZERO = new Limit(BigDecimal.ZERO);

    private final BigDecimal amount;

    public Limit(BigDecimal amount) {
      this.amount = amount;
    }
  }

  @JsonProperty("available_credit_limit")
  private final Limit creditLimit;

  @JsonProperty("available_withdrawal_limit")
  private final Limit withdrawalLimit;

  private Account(Limit creditLimit, Limit withdrawalLimit){
    this.creditLimit = creditLimit;
    this.withdrawalLimit = withdrawalLimit;
  }

  public static Account empty(){
    return new Account(Limit.ZERO, Limit.ZERO);
  }

  public static Optional<Account> fromEntity(Optional<com.pismo.accounts.data.entities.Account> accountEntity){
    return accountEntity.map(acc -> {
      Limit crLimit = new Limit(acc.getCreditLimt());
      Limit drawLimit = new Limit(acc.getWithdrawalLimit());

      return new Account(crLimit, drawLimit);
    });
  }
}
