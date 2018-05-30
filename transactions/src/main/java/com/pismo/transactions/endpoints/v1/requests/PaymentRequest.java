package com.pismo.transactions.endpoints.v1.requests;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentRequest {

  @JsonProperty("account_id")
  @NotNull
  private Long accountId;

  @Min(value = 0, message = "Amount must be positive")
  private BigDecimal amount;

  public Long getAccountId() {
    return accountId;
  }

  public void setAccountId(Long accountId) {
    this.accountId = accountId;
  }

  public BigDecimal getAmount() {
    return amount.add(BigDecimal.ZERO);
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }
}
