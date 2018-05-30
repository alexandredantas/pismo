package com.pismo.transactions.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pismo.transactions.data.entities.OperationType;

public interface OperationTypeRepository  extends JpaRepository<OperationType, Long> {
}
