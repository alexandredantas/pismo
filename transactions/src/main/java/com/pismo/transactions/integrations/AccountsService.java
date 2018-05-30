package com.pismo.transactions.integrations;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Service
public class AccountsService {

  @Value("${accounts.rest.path}")
  private String accountsUrl;

  private RestTemplate accountsApi;

  public enum AccountCheckStates{
    EXISTS, NOTEXISTS, ERROR
  }

  @PostConstruct
  public void setup(){
    this.accountsApi = new RestTemplateBuilder().rootUri(accountsUrl).build();
  }

  @HystrixCommand(fallbackMethod = "circuitOpen")
  public AccountCheckStates checkExistingAccount(Long accountId){
    HttpStatus statusCode = this.accountsApi.getForEntity(accountId.toString(), Object.class).getStatusCode();

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
