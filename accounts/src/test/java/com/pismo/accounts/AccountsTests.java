package com.pismo.accounts;


import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import com.pismo.accounts.data.entities.Account;
import com.pismo.accounts.data.repositories.AccountsRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.HSQL)
public class AccountsTests {

  private MediaType JSON_TYPE = new MediaType(MediaType.APPLICATION_JSON.getType(),
      MediaType.APPLICATION_JSON.getSubtype(),
      Charset.forName("utf8"));

  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private AccountsRepository accountsRepository;

  @Autowired
  private ObjectMapper mapper;

  @Before
  public void setup() throws Exception {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

    this.accountsRepository.deleteAllInBatch();
  }

  @Test
  public void allLimitsEmpty() throws Exception {
    mockMvc.perform(get("/v1/accounts/limits").accept(JSON_TYPE)).andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  public void allLimitsSingleResult() throws Exception {
    accountsRepository.saveAndFlush(Account.empty().copy(BigDecimal.TEN, BigDecimal.ONE));

    mockMvc
        .perform(get("/v1/accounts/limits").accept(JSON_TYPE))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].available_credit_limit.amount", is(BigDecimal.TEN.doubleValue())))
        .andExpect(jsonPath("$[0].available_withdrawal_limit.amount", is(BigDecimal.ONE.doubleValue())));
  }

  @Test
  public void allLimitsMultipleResult() throws Exception {
    IntStream.of(1, 2).mapToObj(BigDecimal::new).forEach(i -> accountsRepository.saveAndFlush(Account.empty().copy(BigDecimal.TEN.multiply(i), BigDecimal.ONE.multiply(i))));

    mockMvc
        .perform(get("/v1/accounts/limits").accept(JSON_TYPE))
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].available_credit_limit.amount", is(10.0)))
        .andExpect(jsonPath("$[0].available_withdrawal_limit.amount", is(1.0)))
        .andExpect(jsonPath("$[1].available_credit_limit.amount", is(20.0)))
        .andExpect(jsonPath("$[1].available_withdrawal_limit.amount", is(2.0)));
  }

  @Test
  public void addAccountAndRetrieve() throws Exception {
    mockMvc
        .perform(post("/v1/accounts").accept(JSON_TYPE))
        .andExpect(status().isOk());

    mockMvc
        .perform(get("/v1/accounts/limits").accept(JSON_TYPE))
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  public void addAccountAndChangeLimits() throws Exception {
    Account newAccount = accountsRepository.save(Account.empty());
    com.pismo.accounts.endpoints.v1.dto.Account newLimits = com.pismo.accounts.endpoints.v1.dto.Account.fromEntity(newAccount.copy(BigDecimal.valueOf(100.0), BigDecimal.valueOf(100.0)));

    mockMvc
        .perform(patch(String.format("/v1/accounts/%d", newAccount.getId()))
            .accept(JSON_TYPE)
            .contentType(JSON_TYPE)
            .content(mapper.writeValueAsString(newLimits)))
        .andExpect(jsonPath("$.previous_limits.available_credit_limit.amount", is(0.0)))
        .andExpect(jsonPath("$.previous_limits.available_withdrawal_limit.amount", is(0.0)))
        .andExpect(jsonPath("$.new_limits.available_credit_limit.amount", is(100.0)))
        .andExpect(jsonPath("$.new_limits.available_withdrawal_limit.amount", is(100.0)));
  }

  @Test
  public void testInvalidLimits() throws Exception {
    Account newAccount = accountsRepository.save(Account.empty().copy(BigDecimal.valueOf(10.0), BigDecimal.valueOf(10.0)));
    com.pismo.accounts.endpoints.v1.dto.Account newLimits = com.pismo.accounts.endpoints.v1.dto.Account.withValues(BigDecimal.valueOf(-100.0), BigDecimal.valueOf(-100.0));

    mockMvc
        .perform(patch(String.format("/v1/accounts/%d", newAccount.getId()))
            .accept(JSON_TYPE)
            .contentType(JSON_TYPE)
            .content(mapper.writeValueAsString(newLimits)))
        .andExpect(jsonPath("$.previous_limits.available_credit_limit.amount", is(10.0)))
        .andExpect(jsonPath("$.previous_limits.available_withdrawal_limit.amount", is(10.0)))
        .andExpect(jsonPath("$.new_limits.available_credit_limit.amount", is(10.0)))
        .andExpect(jsonPath("$.new_limits.available_withdrawal_limit.amount", is(10.0)));
  }

}
