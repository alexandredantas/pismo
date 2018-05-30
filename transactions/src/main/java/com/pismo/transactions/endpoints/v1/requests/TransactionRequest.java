package com.pismo.transactions.endpoints.v1.requests;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pismo.transactions.data.entities.Transactions;

public class TransactionRequest {

  public enum OpType{
    A_VISTA(Transactions.OperationType.COMPRA_A_VISTA),
    PARCELADA(Transactions.OperationType.COMPRA_PARCELADA),
    SAQUE(Transactions.OperationType.SAQUE),
    PAGAMENTO(Transactions.OperationType.PAGAMENTO)
    ;

    private static final Map<Integer, OpType> TYPES = Arrays
        .stream(OpType.values())
        .collect(Collectors.toMap(op -> op.getOperationType().getId(), Function.identity()));

    private final Transactions.OperationType operationType;


    OpType(Transactions.OperationType operationType) {
      this.operationType = operationType;
    }

    public Transactions.OperationType getOperationType() {
      return operationType;
    }

    //I'm throwing an exception here to force 400 status code
    @JsonCreator
    public OpType fromCode(int code){
      if (!TYPES.containsKey(code)){
        throw new IllegalArgumentException("Invalid operation code");
      }

      return TYPES.get(code);
    }
  }

  @JsonProperty("account_id")
  private Long accountId;

  @JsonProperty("operation_type_id")
  private OpType operationId;

  @Min(value = 0L, message = "Amount must be higher or equal to 0")
  private BigDecimal amount;

  public TransactionRequest() {
  }

  public TransactionRequest(Long accountId, OpType operationId, BigDecimal amount) {
    this.accountId = accountId;
    this.operationId = operationId;
    this.amount = amount;
  }

  public Long getAccountId() {
    return accountId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public Transactions.OperationType getOperationType(){
    return this.operationId.getOperationType();
  }
}