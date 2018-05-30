package com.pismo.transactions.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.pismo.transactions.data.entities.PaymentsTracking;
import com.pismo.transactions.data.entities.Transaction;
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

  @Transactional
  public Optional<Integer> processPayments(List<PaymentRequest> payments) {
    int writtenResult = payments.stream()
        .map(this::processPayment)
        .map(payment -> Pair.of(transactionsRepository.saveAndFlushOpt(payment.getLeft()), payment.getRight()))
        .map(this::paidRecordToEntity)
        .filter(records -> !records.isEmpty())
        .map(paymentsTrackingRepository::saveAll)
        .map(List::size)
        .reduce((a, b) -> a + b)
        .orElse(0);

    if (writtenResult == 0) {
      TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
      return Optional.empty();
    } else {
      return Optional.of(writtenResult);
    }
  }

  private Pair<Transaction, List<Transaction.PaidRecord>> processPayment(PaymentRequest request) {
    return settleDebts(Stream.ofAll(transactionsRepository.findExistingDebts(request.getAccountId())), request.getAmount(), request.getAmount(), request.getAccountId(), new ArrayList<>()).invoke();
  }

  private TailCall<Pair<Transaction, List<Transaction.PaidRecord>>> settleDebts(Stream<Transaction> debts, BigDecimal amount, BigDecimal remainder, Long accountId, List<Transaction.PaidRecord> paidRecords) {
    if (debts.isEmpty() || remainder.equals(BigDecimal.ZERO)) {

      Transaction payment = new Transaction();
      payment.setOperationType(4);
      payment.setEventDate(LocalDateTime.now());
      payment.setDueDate(LocalDateTime.now().plusMonths(1).withDayOfMonth(5));
      payment.setAmount(amount);
      payment.setBalance(remainder);
      payment.setAccountId(accountId);

      return TailCalls.done(Pair.of(payment, paidRecords));
    } else {
      Transaction transaction = debts.head();
      Pair<BigDecimal, Transaction.PaidRecord> balanceResult = transaction.addToBalance(remainder);
      paidRecords.add(balanceResult.getRight());
      this.transactionsRepository.saveAndFlush(transaction);
      return () -> settleDebts(debts.tail(), amount, balanceResult.getLeft(), accountId, paidRecords);
    }
  }

  private List<PaymentsTracking> paidRecordToEntity(Pair<Optional<Transaction>, List<Transaction.PaidRecord>> records) {
    return records.getLeft().map(credit -> records.getRight().stream()
        .map(payment -> {
          PaymentsTracking tracking = new PaymentsTracking();

          tracking.setAmount(payment.getAmount());
          tracking.setCreditTransactionId(credit.getId());
          tracking.setDebitTransactionId(payment.getTransactionId());

          return tracking;
        }).collect(Collectors.toList())
    ).orElse(Collections.emptyList());
  }
}
