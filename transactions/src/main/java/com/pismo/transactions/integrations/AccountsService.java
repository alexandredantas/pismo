package com.pismo.transactions.integrations;

public interface AccountsService {

  enum AccountCheckStates{
    EXISTS, NOTEXISTS, ERROR
  }

  AccountCheckStates checkExistingAccount(Long accountId);
}
