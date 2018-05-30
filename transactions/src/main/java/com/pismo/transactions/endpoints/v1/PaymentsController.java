package com.pismo.transactions.endpoints.v1;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pismo.transactions.endpoints.v1.requests.PaymentRequest;
import com.pismo.transactions.services.PaymentsService;

@RestController
@RequestMapping("/v1/payments")
public class PaymentsController {

  @Autowired
  private PaymentsService paymentsService;

  @PostMapping
  public ResponseEntity<?> payDebts(@Validated @RequestBody List<PaymentRequest> payments){
    return paymentsService.processPayments(payments)
        .filter(written -> written > 0)
        .map(a -> ResponseEntity.ok().build())
        .orElse(ResponseEntity.status(500).build());
  }

}
