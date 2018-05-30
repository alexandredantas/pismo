package com.pismo.transactions.data.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.tuple.Pair;

@Entity
@Table(name = "transactions", schema = "transactions")
public class Transactions {

  public class PaidRecord{
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

  public enum OperationType{
    DESCONHECIDO(-1, -1),
    COMPRA_A_VISTA(1, 2),
    COMPRA_PARCELADA(2,1),
    SAQUE(3,0),
    PAGAMENTO(4,0)
    ;

    private static final Map<Integer, OperationType> TYPES = Arrays
        .stream(OperationType.values())
        .collect(Collectors.toMap(OperationType::getId, Function.identity()));

    private int id;
    private int chargeOrder;

    OperationType(int id, int chargeOrder) {
      this.id = id;
      this.chargeOrder = chargeOrder;
    }

    public int getId() {
      return id;
    }

    public int getChargeOrder() {
      return chargeOrder;
    }

    public static OperationType byId(int id){
      return TYPES.getOrDefault(id, DESCONHECIDO);
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
  @Max(value = 0)
  private BigDecimal balance;

  @NotNull
  @Column(name = "event_date")
  private LocalDateTime eventDate;

  @NotNull
  @Column(name = "due_date")
  private LocalDateTime dueDate;

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

  public OperationType getOperationType(){
    return OperationType.byId(this.operationType);
  }

  public void setOperationType(OperationType type){
    this.operationType = type.id;
  }

  public Pair<BigDecimal, PaidRecord> addToBalance(BigDecimal value){
    if (value.doubleValue() < 0){
      throw new IllegalArgumentException("Pass a positive value!");
    }

    BigDecimal settle = this.balance.add(value);

    if (settle.doubleValue() > 0){
      PaidRecord record = new PaidRecord(this.id, this.balance.negate());
      this.balance = BigDecimal.ZERO;
      return Pair.of(settle, record);
    } else{
      PaidRecord record = new PaidRecord(this.id, value.plus());
      return Pair.of(BigDecimal.ZERO, record);
    }
  }
}
