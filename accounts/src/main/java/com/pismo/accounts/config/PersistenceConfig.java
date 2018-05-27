package com.pismo.accounts.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.pismo.accounts.data.repositories")
@EntityScan(basePackages = "com.pismo.accounts.data.entities")
public class PersistenceConfig {

}
