package com.pismo.transactions.endpoints.v1.requests;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pismo.transactions.data.entities.Transaction;

public class TransactionRequest {

  public enum OpType {
    A_VISTA(1),
    PARCELADA(2),
    SAQUE(3),
    PAGAMENTO(4);

    private static final Map<Integer, OpType> TYPES = Arrays
        .stream(OpType.values())
        .collect(Collectors.toMap(OpType::getOperationType, Function.identity()));

    private final int operationTypeId;


    OpType(int operationType) {
      this.operationTypeId = operationType;
    }

    public int getOperationType() {
      return operationTypeId;
    }

    //I'm throwing an exception here to force 400 status code
    @JsonCreator
    public OpType fromCode(int code) {
      if (!TYPES.containsKey(code)) {
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

  public int getOperationType() {
    return this.operationId.getOperationType();
  }
}
