package com.pismo.transactions.endpoints.v1;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pismo.transactions.data.entities.Transaction;
import com.pismo.transactions.data.repositories.TransactionsRepository;
import com.pismo.transactions.endpoints.v1.requests.TransactionRequest;
import com.pismo.transactions.services.AccountsService;
import com.pismo.transactions.services.impl.AccountsServiceImpl;

@RestController
@RequestMapping(path = "/v1/transactions")
public class TransactionsController {

  @Autowired
  private AccountsService accountsService;

  @Autowired
  private TransactionsRepository transactionsRepository;

  @PostMapping
  public ResponseEntity<?> newTransaction(@Validated @RequestBody TransactionRequest request){
    return Optional.of(accountsService.checkExistingAccount(request.getAccountId()))
        .filter(state -> !AccountsServiceImpl.AccountCheckStates.NOTEXISTS.equals(state))
        .map(a -> requestToEntity(request))
        .flatMap(transactionsRepository::saveAndFlushOpt)
        .map(a -> ResponseEntity.ok().build())
        .orElseGet(() -> {
          /*
          This sleep is ugly, i know! But the idea is avoiding atackers to enumerate an account posting dummy requests. If all requests
          take the same time approximately, an attacker is not able to discover if he hit a valid account or not
           */
          try {
            Thread.sleep(300);
          }catch(Exception e){
          }
          return ResponseEntity.status(400).build();
        });
  }


  private Transaction requestToEntity(TransactionRequest request){
    Transaction entity = new Transaction();
    entity.setAccountId(request.getAccountId());
    entity.setAmount(request.getAmount().negate());
    entity.setBalance(request.getAmount().negate());
    entity.setEventDate(LocalDateTime.now());

    //Hardcoding 5th day of next month from now as due date. Seriously, i'll not make this project more complex for a date ;)
    entity.setDueDate(LocalDateTime.now().plusMonths(1).withDayOfMonth(5));
    entity.setOperationType(request.getOperationType());

    return entity;
  }
}
