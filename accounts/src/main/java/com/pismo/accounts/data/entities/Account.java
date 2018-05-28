package com.pismo.accounts.data.entities;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "accounts", schema = "accounts")
public class Account {

  @Id
  @GeneratedValue
  private Long id;

  @Column(name = "credit_limit")
  private BigDecimal creditLimt;

  @Column(name = "withdrawal_limit")
  private BigDecimal withdrawalLimit;

  public Long getId() {
    return id;
  }

  public BigDecimal getCreditLimt() {
    return creditLimt;
  }

  public BigDecimal getWithdrawalLimit() {
    return withdrawalLimit;
  }

  public static Account empty(){
    Account acc = new Account();
    acc.creditLimt = BigDecimal.ZERO;
    acc.withdrawalLimit = BigDecimal.ZERO;

    return acc;
  }

  private void setCreditLimt(BigDecimal creditLimt) {
    BigDecimal newLimit = this.creditLimt.add(creditLimt);

    if (newLimit.doubleValue() >= 0) {
      this.creditLimt = creditLimt;
    }
  }

  private void setWithdrawalLimit(BigDecimal withdrawalLimit) {
    BigDecimal newDraw = this.withdrawalLimit.add(withdrawalLimit);

    if (newDraw.doubleValue() >= 0) {
      this.withdrawalLimit = withdrawalLimit;
    }
  }

  public Account copy(BigDecimal newCreditLimit, BigDecimal newWithdrawalLimit){
    Account newAccount = new Account();
    newAccount.id = this.id;
    newAccount.setCreditLimt(newCreditLimit);
    newAccount.setWithdrawalLimit(newWithdrawalLimit);

    return newAccount;
  }
}
