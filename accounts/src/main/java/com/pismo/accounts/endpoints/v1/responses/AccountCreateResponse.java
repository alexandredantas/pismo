package com.pismo.accounts.endpoints.v1.responses;

public class AccountCreateResponse {
  private final Long id;

  public AccountCreateResponse(Long id){
    this.id = id;
  }

  public static AccountCreateResponse empty(){
    return new AccountCreateResponse(null);
  }
}
