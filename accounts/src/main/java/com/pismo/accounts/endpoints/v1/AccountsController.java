package com.pismo.accounts.endpoints.v1;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.pismo.accounts.endpoints.v1.dto.Account;
import com.pismo.accounts.endpoints.v1.responses.AccountCreateResponse;
import com.pismo.accounts.endpoints.v1.responses.ChangeLimitResponse;
import com.pismo.accounts.services.AccountsService;

@RestController
@RequestMapping("/v1/accounts")
public class AccountsController {

  @Autowired
  private AccountsService accountsService;

  @RequestMapping(method = RequestMethod.POST)
  @ResponseBody
  public AccountCreateResponse createAccount(){
    return accountsService
        .create()
        .map(acc -> new AccountCreateResponse(acc.getId()))
        .orElseGet(AccountCreateResponse::empty);
  }

  @RequestMapping(path = "/{accountId}", method = RequestMethod.PATCH)
  @ResponseBody
  public ChangeLimitResponse changeLimits(@PathVariable("accountId") Long accountId, @RequestBody Account changeLimit){
    return accountsService
        .findById(accountId)
        .map(found -> Pair.of(found, accountsService.updateLimits(found.copy(changeLimit.getCreditLimit().getAmount(), changeLimit.getWithdrawalLimit().getAmount()))))
        .map(tuple -> new ChangeLimitResponse(Account.fromEntity(tuple.getLeft()), Account.fromEntityOpt(tuple.getRight())))
        .orElseGet(ChangeLimitResponse::empty);
  }

  @RequestMapping(path = "/{accountId}/limits", method = RequestMethod.GET)
  @ResponseBody
  public Optional<Account> accountLimits(@PathVariable("accountId") Long accountId){
    return accountsService.findById(accountId).map(Account::fromEntity);
  }

  @RequestMapping(path = "/limits", method = RequestMethod.GET)
  @ResponseBody
  public List<Account> allLimits(){
    return accountsService.retrieveAll().stream().map(Account::fromEntity).collect(Collectors.toList());
  }
}
