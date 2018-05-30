package com.pismo.transactions.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pismo.transactions.data.entities.PaymentsTracking;
import com.pismo.transactions.data.entities.Transactions;
import com.pismo.transactions.data.repositories.PaymentsTrackingRepository;
import com.pismo.transactions.data.repositories.TransactionsRepository;
import com.pismo.transactions.endpoints.v1.requests.PaymentRequest;
import com.pismo.transactions.util.TailCall;
import com.pismo.transactions.util.TailCalls;

import io.vavr.collection.Stream;

@Service
public class PaymentsService {

  @Autowired
  private TransactionsRepository transactionsRepository;

  @Autowired
  private PaymentsTrackingRepository paymentsTrackingRepository;

  public Optional<Integer> processPayments(List<PaymentRequest> payments) {
    return payments.stream()
        .map(this::processPayment)
        .map(payment -> Pair.of(transactionsRepository.saveAndFlush(payment.getLeft()), payment.getRight()))
        .map(this::paidRecordToEntity)
        .map(paymentsTrackingRepository::saveAll)
        .map(List::size)
        .reduce((a, b) -> a + b);
  }

  private Pair<Transactions, List<Transactions.PaidRecord>> processPayment(PaymentRequest request) {
    return settleDebts(Stream.ofAll(transactionsRepository.findExistingDebts(request.getAccountId())), request.getAmount(), request.getAmount(), request.getAccountId(), new ArrayList<>()).invoke();
  }

  private TailCall<Pair<Transactions, List<Transactions.PaidRecord>>> settleDebts(Stream<Transactions> debts, BigDecimal amount, BigDecimal remainder, Long accountId, List<Transactions.PaidRecord> paidRecords) {
    if (debts.isEmpty() || remainder.equals(BigDecimal.ZERO)) {

      Transactions payment = new Transactions();
      payment.setOperationType(Transactions.OperationType.PAGAMENTO);
      payment.setEventDate(LocalDateTime.now());
      payment.setDueDate(LocalDateTime.now().plusMonths(1).withDayOfMonth(5));
      payment.setAmount(amount);
      payment.setBalance(remainder);
      payment.setAccountId(accountId);

      return TailCalls.done(Pair.of(payment, paidRecords));
    } else {
      Transactions transaction = debts.head();
      Pair<BigDecimal, Transactions.PaidRecord> balanceResult = transaction.addToBalance(remainder);
      paidRecords.add(balanceResult.getRight());
      this.transactionsRepository.saveAndFlush(transaction);
      return () -> settleDebts(debts.tail(), amount, balanceResult.getLeft(), accountId, paidRecords);
    }
  }

  private List<PaymentsTracking> paidRecordToEntity(Pair<Transactions, List<Transactions.PaidRecord>> records) {
    return records.getRight().stream()
        .map(payment -> {
          PaymentsTracking tracking = new PaymentsTracking();

          tracking.setAmount(payment.getAmount());
          tracking.setCreditTransactionId(records.getLeft().getId());
          tracking.setDebitTransactionId(payment.getTransactionId());

          return tracking;
        }).collect(Collectors.toList());
  }
}
