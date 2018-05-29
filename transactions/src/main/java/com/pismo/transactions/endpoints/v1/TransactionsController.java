package com.pismo.transactions.endpoints.v1;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pismo.transactions.endpoints.v1.requests.TransactionRequest;

@RestController
@RequestMapping(path = "/v1/transactions")
public class TransactionsController {

  @PostMapping
  public ResponseEntity<?> newTransaction(@RequestBody TransactionRequest request){

    return ResponseEntity.ok().build();
  }

}
