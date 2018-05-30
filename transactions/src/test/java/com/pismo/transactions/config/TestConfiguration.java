package com.pismo.transactions.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.pismo.transactions.services.AccountsService;

@Configuration
@Profile("test")
public class TestConfiguration {

  @Bean
  public AccountsService mockAccountsService(){
    return Mockito.mock(AccountsService.class);
  }
}
