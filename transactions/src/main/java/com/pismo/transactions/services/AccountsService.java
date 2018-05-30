package com.pismo.transactions.services;

public interface AccountsService {

  enum AccountCheckStates{
    EXISTS, NOTEXISTS, ERROR
  }

  AccountCheckStates checkExistingAccount(Long accountId);
}
