package com.pismo.accounts.services;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pismo.accounts.data.entities.Account;
import com.pismo.accounts.data.repositories.AccountsRepository;

public class AccountsService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AccountsService.class);

  @Autowired
  private AccountsRepository accountsRepository;

  public Optional<Account> findById(Long accountId){
    return this.accountsRepository.findById(accountId);
  }

  public Optional<Account> updateLimits(Account newLimits){
    try {
      return accountsRepository
          .findById(newLimits.getId())
          .map(existingAcc -> existingAcc.copy(newLimits.getCreditLimt(), newLimits.getWithdrawalLimit()))
          .map(accountsRepository::saveAndFlush);
    } catch(Exception e){
      LOGGER.error("Error updating account limits for account={}", newLimits.getId(), e);
      return Optional.empty();
    }
  }

  public Optional<Account> create(){
    try{
      return Optional.of(this.accountsRepository.save(Account.empty()));
    }catch(Exception e){
      LOGGER.error("Error creating new account", e);
      return Optional.empty();
    }
  }
}
