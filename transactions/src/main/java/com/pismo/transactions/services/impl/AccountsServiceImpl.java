package com.pismo.transactions.services.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.pismo.transactions.services.AccountsService;

@Service
@Profile("!test")
public class AccountsServiceImpl implements AccountsService {

  @Value("${accounts.rest.path}")
  private String accountsUrl;

  private RestTemplate accountsApi;

  @PostConstruct
  public void setup(){
    this.accountsApi = new RestTemplateBuilder().rootUri(accountsUrl).build();
  }

  @Override
  @HystrixCommand(fallbackMethod = "circuitOpen")
  public AccountCheckStates checkExistingAccount(Long accountId){
    HttpStatus statusCode = this.accountsApi.getForEntity("/".concat(accountId.toString()), Object.class).getStatusCode();

    //Miss you pattern matching over sealed traits :(
    if (statusCode.is2xxSuccessful()){
      return AccountCheckStates.EXISTS;
    } else{
      if (statusCode.is4xxClientError()){
        return AccountCheckStates.NOTEXISTS;
      }
    }

    return AccountCheckStates.ERROR;
  }

  private AccountCheckStates circuitOpen(){
    return AccountCheckStates.ERROR;
  }
}
