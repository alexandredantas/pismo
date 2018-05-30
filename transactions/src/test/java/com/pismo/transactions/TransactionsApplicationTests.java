package com.pismo.transactions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pismo.transactions.data.repositories.TransactionsRepository;
import com.pismo.transactions.endpoints.v1.requests.TransactionRequest;
import com.pismo.transactions.integrations.AccountsService;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.HSQL)
public class TransactionsApplicationTests {

  private MediaType JSON_TYPE = new MediaType(MediaType.APPLICATION_JSON.getType(),
      MediaType.APPLICATION_JSON.getSubtype(),
      Charset.forName("utf8"));

  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private ObjectMapper mapper;

  @Autowired
  private AccountsService mockAccountsService;

  @Autowired
  private TransactionsRepository transactionsRepository;

  @Before
  public void setup() throws Exception {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    transactionsRepository.deleteAllInBatch();
    Mockito.reset(mockAccountsService);
  }

  @Test
  public void postValidTransaction() throws Exception {
    TransactionRequest request = new TransactionRequest(1L, TransactionRequest.OpType.A_VISTA, BigDecimal.ONE);
    Mockito.when(mockAccountsService.checkExistingAccount(1L)).thenReturn(AccountsService.AccountCheckStates.EXISTS);

    mockMvc.perform(post("/v1/transactions")
        .contentType(JSON_TYPE)
        .content(mapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }

  @Test
  public void postInvalidTransaction() throws Exception {
    TransactionRequest request = new TransactionRequest(1L, TransactionRequest.OpType.A_VISTA, BigDecimal.valueOf(-1));
    Mockito.when(mockAccountsService.checkExistingAccount(1L)).thenReturn(AccountsService.AccountCheckStates.EXISTS);

    mockMvc.perform(post("/v1/transactions")
        .contentType(JSON_TYPE)
        .content(mapper.writeValueAsString(request)))
        .andExpect(status().is(400));
  }

  @Test
  public void postTransactionForMissingAccount() throws Exception {
    TransactionRequest request = new TransactionRequest(1L, TransactionRequest.OpType.A_VISTA, BigDecimal.TEN);
    Mockito.when(mockAccountsService.checkExistingAccount(1L)).thenReturn(AccountsService.AccountCheckStates.NOTEXISTS);

    mockMvc.perform(post("/v1/transactions")
        .contentType(JSON_TYPE)
        .content(mapper.writeValueAsString(request)))
        .andExpect(status().is(400));
  }
}
