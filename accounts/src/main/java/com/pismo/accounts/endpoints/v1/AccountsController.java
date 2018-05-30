package com.pismo.accounts.endpoints.v1;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

  @PostMapping
  public AccountCreateResponse createAccount() {
    return accountsService
        .create()
        .map(acc -> new AccountCreateResponse(acc.getId()))
        .orElseGet(AccountCreateResponse::empty);
  }

  @PatchMapping(path = "/{accountId}")
  public ChangeLimitResponse changeLimits(@PathVariable("accountId") Long accountId, @RequestBody Account changeLimit) {
    return accountsService
        .findById(accountId)
        .map(found -> Pair.of(Account.fromEntity(found), accountsService
            .updateLimits(found.copy(changeLimit.getCreditLimit().getAmount(), changeLimit.getWithdrawalLimit().getAmount()))
            .map(Account::fromEntity)
            .orElse(Account.fromEntity(found))))
        .map(tuple -> new ChangeLimitResponse(tuple.getLeft(), tuple.getRight()))
        .orElseGet(ChangeLimitResponse::empty);
  }

  @GetMapping(path = "/{accountId}/limits")
  public Optional<Account> accountLimits(@PathVariable("accountId") Long accountId) {
    return accountsService
        .findById(accountId)
        .map(Account::fromEntity);
  }

  @GetMapping(path = "/{accountId}")
  public ResponseEntity<?> existingAccount(@PathVariable("accountId") Long accountId) {
    return accountsService
        .findById(accountId)
        .map(a -> ResponseEntity.ok().build())
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping(path = "/limits")
  public List<Account> allLimits() {
    return accountsService
        .retrieveAll()
        .stream()
        .map(Account::fromEntity)
        .collect(Collectors.toList());
  }
}
