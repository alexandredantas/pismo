package com.pismo.accounts.data.entities;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "accounts", schema = "accounts")
public class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "credit_limit")
  private BigDecimal creditLimit;

  @Column(name = "withdrawal_limit")
  private BigDecimal withdrawalLimit;

  private Account() {
    this.creditLimit = BigDecimal.ZERO;
    this.withdrawalLimit = BigDecimal.ZERO;
  }

  public Long getId() {
    return id;
  }

  public BigDecimal getCreditLimit() {
    return creditLimit;
  }

  public BigDecimal getWithdrawalLimit() {
    return withdrawalLimit;
  }

  public static Account empty(){
    return new Account();
  }

  private void setCreditLimit(BigDecimal creditLimit) {
    BigDecimal newLimit = this.creditLimit.add(creditLimit);

    if (newLimit.doubleValue() >= 0) {
      this.creditLimit = creditLimit;
    }
  }

  private void setWithdrawalLimit(BigDecimal withdrawalLimit) {
    BigDecimal newDraw = this.withdrawalLimit.add(withdrawalLimit);

    if (newDraw.doubleValue() >= 0) {
      this.withdrawalLimit = withdrawalLimit;
    }
  }

  //How I miss you, Scala case class copy :(
  public Account copy(BigDecimal newCreditLimit, BigDecimal newWithdrawalLimit){
    Account newAccount = new Account();
    newAccount.id = this.id;
    newAccount.creditLimit = this.creditLimit;
    newAccount.withdrawalLimit = this.withdrawalLimit;

    newAccount.setCreditLimit(newCreditLimit);
    newAccount.setWithdrawalLimit(newWithdrawalLimit);

    return newAccount;
  }
}
