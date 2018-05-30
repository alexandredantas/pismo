package com.pismo.transactions.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pismo.transactions.data.entities.PaymentsTracking;

public interface PaymentsTrackingRepository extends JpaRepository<PaymentsTracking, Long> {
}
