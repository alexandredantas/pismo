package com.pismo.accounts.endpoints.v1.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Account {

  public static class Limit {

    private static final Limit ZERO = new Limit(BigDecimal.ZERO);

    private BigDecimal amount;

    public Limit() {
    }

    public Limit(BigDecimal amount) {
      this.amount = amount;
    }

    public BigDecimal getAmount() {
      return amount;
    }
  }

  @JsonProperty("available_credit_limit")
  private Limit creditLimit;

  @JsonProperty("available_withdrawal_limit")
  private Limit withdrawalLimit;

  public Account() {
  }

  private Account(Limit creditLimit, Limit withdrawalLimit){
    this.creditLimit = creditLimit;
    this.withdrawalLimit = withdrawalLimit;
  }

  public static Account empty(){
    return new Account(Limit.ZERO, Limit.ZERO);
  }

  public static Account fromEntity(com.pismo.accounts.data.entities.Account accountEntity){
    Limit crLimit = new Limit(accountEntity.getCreditLimit());
    Limit drawLimit = new Limit(accountEntity.getWithdrawalLimit());

    return new Account(crLimit, drawLimit);
  }

  public static Account withValues(BigDecimal creditLimit, BigDecimal withdrawalLimit){
    return new Account(new Limit(creditLimit), new Limit(withdrawalLimit));
  }

  public Limit getCreditLimit() {
    return creditLimit;
  }

  public Limit getWithdrawalLimit() {
    return withdrawalLimit;
  }
}
