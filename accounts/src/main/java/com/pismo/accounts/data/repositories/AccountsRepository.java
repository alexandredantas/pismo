package com.pismo.accounts.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pismo.accounts.data.entities.Account;

public interface AccountsRepository extends JpaRepository<Account, Long> {
}
