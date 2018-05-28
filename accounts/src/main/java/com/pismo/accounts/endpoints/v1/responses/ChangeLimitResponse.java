package com.pismo.accounts.endpoints.v1.responses;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pismo.accounts.endpoints.v1.dto.Account;

public class ChangeLimitResponse {

  @JsonProperty("previous_limits")
  private final Account previousLimits;

  @JsonProperty("new_limits")
  private final Optional<Account> newLimits;

  public ChangeLimitResponse(Account previousLimits, Optional<Account> newLimits) {
    this.previousLimits = previousLimits;
    this.newLimits = newLimits;
  }

  public static ChangeLimitResponse empty(){
    return new ChangeLimitResponse(Account.empty(), Optional.of(Account.empty()));
  }
}
