package com.pismo.transactions.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.pismo.transactions.data.repositories")
@EntityScan(basePackages = "com.pismo.transactions.data.entities")
public class PersistenceConfig {

}
