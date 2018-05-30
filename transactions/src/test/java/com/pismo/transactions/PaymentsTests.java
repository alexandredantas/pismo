package com.pismo.transactions;

import static com.pismo.transactions.util.TestUtils.JSON_TYPE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pismo.transactions.data.entities.Transaction;
import com.pismo.transactions.data.repositories.PaymentsTrackingRepository;
import com.pismo.transactions.data.repositories.TransactionsRepository;
import com.pismo.transactions.endpoints.v1.requests.PaymentRequest;


@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.HSQL)
public class PaymentsTests {

  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private PaymentsTrackingRepository paymentsRepository;

  @Autowired
  private TransactionsRepository transactionsRepository;

  @Autowired
  private ObjectMapper mapper;

  @Before
  public void setup() throws Exception {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    transactionsRepository.deleteAll();
    paymentsRepository.deleteAll();
  }

  private List<Transaction> prepareTestData() {
    Transaction first = new Transaction();
    Transaction second = new Transaction();
    Transaction third = new Transaction();

    first.setAccountId(1L);
    first.setAmount(new BigDecimal(-50));
    first.setBalance(new BigDecimal(-50));
    first.setEventDate(LocalDate.of(2017, 4, 4).atStartOfDay());
    first.setDueDate(LocalDate.of(2017, 5, 10).atStartOfDay());
    first.setOperationType(3);

    second.setAccountId(1L);
    second.setAmount(new BigDecimal(-23.5));
    second.setBalance(new BigDecimal(-23.5));
    second.setEventDate(LocalDate.of(2017, 4, 10).atStartOfDay());
    second.setDueDate(LocalDate.of(2017, 5, 10).atStartOfDay());
    second.setOperationType(1);

    third.setAccountId(1L);
    third.setAmount(new BigDecimal(-18.7));
    third.setBalance(new BigDecimal(-18.7));
    third.setEventDate(LocalDate.of(2017, 4, 30).atStartOfDay());
    third.setDueDate(LocalDate.of(2017, 6, 10).atStartOfDay());
    third.setOperationType(1);

    return Arrays.asList(first, second, third);
  }

  @Test
  public void singlePaymentTest() throws Exception {
    transactionsRepository.saveAll(prepareTestData());

    PaymentRequest request = new PaymentRequest();
    request.setAccountId(1L);
    request.setAmount(new BigDecimal(70));

    mockMvc.perform(post("/v1/payments")
        .contentType(JSON_TYPE)
        .content(mapper.writeValueAsString(Collections.singletonList(request))))
        .andExpect(status().isOk());

    List<Transaction> debts = transactionsRepository.findAll();

    BigDecimal amount = debts.stream().map(Transaction::getAmount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    BigDecimal debtValue = debts.stream().map(Transaction::getBalance).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

    Assert.assertEquals(amount, debtValue);
  }

  @Test
  public void testMultiplePaymentsWithCredit() throws Exception{
    transactionsRepository.saveAll(prepareTestData());

    PaymentRequest request1 = new PaymentRequest();
    request1.setAccountId(1L);
    request1.setAmount(new BigDecimal(73.5));

    PaymentRequest request2 = new PaymentRequest();
    request2.setAccountId(1L);
    request2.setAmount(new BigDecimal(30.7));

    mockMvc.perform(post("/v1/payments")
        .contentType(JSON_TYPE)
        .content(mapper.writeValueAsString(Arrays.asList(request1, request2))))
        .andExpect(status().isOk());

    List<Transaction> debts = transactionsRepository.findAll();

    BigDecimal debtValue = debts.stream().map(Transaction::getBalance).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    BigDecimal amount = debts.stream().map(Transaction::getAmount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

    Assert.assertEquals(amount, debtValue);
  }
}
