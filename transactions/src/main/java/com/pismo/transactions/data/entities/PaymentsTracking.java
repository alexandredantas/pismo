package com.pismo.transactions.data.entities;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "payments_tracking", schema = "transactions")
public class PaymentsTracking {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Column(name = "credit_transaction_id")
  private Long creditTransactionId;

  @NotNull
  @Column(name = "debit_transaction_id")
  private Long debitTransactionId;

  @NotNull
  @Column(name = "amount")
  private BigDecimal amount;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getCreditTransactionId() {
    return creditTransactionId;
  }

  public void setCreditTransactionId(Long creditTransactionId) {
    this.creditTransactionId = creditTransactionId;
  }

  public Long getDebitTransactionId() {
    return debitTransactionId;
  }

  public void setDebitTransactionId(Long debitTransactionId) {
    this.debitTransactionId = debitTransactionId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }
}
