package com.pismo.accounts.endpoints.v1;

import java.util.Optional;

import javax.websocket.server.PathParam;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
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

  @RequestMapping(path = "/${accountId}", method = RequestMethod.PATCH)
  @ResponseBody
  public ChangeLimitResponse changeLimits(@PathParam("accountId") Long accountId, @RequestBody Account changeLimit){
    return accountsService
        .findById(accountId)
        .map(found -> Pair.of(Optional.of(found), accountsService.updateLimits(found)))
        .map(tuple -> new ChangeLimitResponse(Account.fromEntity(tuple.getLeft()), Account.fromEntity(tuple.getRight())))
        .orElseGet(ChangeLimitResponse::empty);
  }
}
