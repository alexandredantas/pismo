package com.pismo.transactions.endpoints.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pismo.transactions.endpoints.v1.requests.TransactionRequest;
import com.pismo.transactions.integrations.AccountsService;

@RestController
@RequestMapping(path = "/v1/transactions")
public class TransactionsController {

  @Autowired
  private AccountsService accountsService;

  @PostMapping
  public ResponseEntity<?> newTransaction(@RequestBody TransactionRequest request){

    return ResponseEntity.ok().build();
  }

}
