package com.pismo.accounts.endpoints.v1.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pismo.accounts.endpoints.v1.dto.Account;

public class ChangeLimitResponse {

  @JsonProperty("previous_limits")
  private final Account previousLimits;

  @JsonProperty("new_limits")
  private final Account newLimits;

  public ChangeLimitResponse(Account previousLimits, Account newLimits) {
    this.previousLimits = previousLimits;
    this.newLimits = newLimits;
  }

  public static ChangeLimitResponse empty(){
    return new ChangeLimitResponse(Account.empty(), Account.empty());
  }
}
