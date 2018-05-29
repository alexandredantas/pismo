package com.pismo.accounts.endpoints.v1.responses;

public class AccountCreateResponse {
  private Long id;

  public AccountCreateResponse(Long id){
    this.id = id;
  }

  public static AccountCreateResponse empty(){
    return new AccountCreateResponse(null);
  }

  public Long getId() {
    return id;
  }
}
