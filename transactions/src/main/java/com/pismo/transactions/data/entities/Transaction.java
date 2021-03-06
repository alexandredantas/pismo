package com.pismo.transactions.data.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.tuple.Pair;

@Entity
@Table(name = "transactions", schema = "transactions")
public class Transaction {

  public class PaidRecord {
    private final Long transactionId;
    private final BigDecimal amount;

    public PaidRecord(Long transactionId, BigDecimal amount) {
      this.transactionId = transactionId;
      this.amount = amount;
    }

    public Long getTransactionId() {
      return transactionId;
    }

    public BigDecimal getAmount() {
      return amount;
    }
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Column(name = "account_id")
  private Long accountId;

  @NotNull
  @Column(name = "operation_type")
  private int operationType;

  @NotNull
  @Column(name = "amount")
  private BigDecimal amount;

  @NotNull
  @Column(name = "balance")
  private BigDecimal balance;

  @NotNull
  @Column(name = "event_date")
  private LocalDateTime eventDate;

  @NotNull
  @Column(name = "due_date")
  private LocalDateTime dueDate;

  @OneToOne
  @JoinColumn(name = "operation_type", insertable = false, updatable = false)
  private OperationType opType;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getAccountId() {
    return accountId;
  }

  public void setAccountId(Long accountId) {
    this.accountId = accountId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

  public LocalDateTime getEventDate() {
    return eventDate;
  }

  public void setEventDate(LocalDateTime eventDate) {
    this.eventDate = eventDate;
  }

  public LocalDateTime getDueDate() {
    return dueDate;
  }

  public void setDueDate(LocalDateTime dueDate) {
    this.dueDate = dueDate;
  }

  public void setOperationType(int type) {
    this.operationType = type;
  }

  public int getOperationType() {
    return operationType;
  }

  public Pair<BigDecimal, PaidRecord> addToBalance(BigDecimal value) {
    if (value.doubleValue() < 0) {
      throw new IllegalArgumentException("Pass a positive value!");
    }

    BigDecimal settle = this.balance.add(value);

    if (settle.doubleValue() > 0) {
      PaidRecord record = new PaidRecord(this.id, this.balance.negate());
      this.balance = BigDecimal.ZERO;
      return Pair.of(settle, record);
    } else {
      PaidRecord record = new PaidRecord(this.id, value.plus());
      this.balance = this.balance.add(value);
      return Pair.of(BigDecimal.ZERO, record);
    }
  }
}
