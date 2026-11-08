package com.payflow.repository;

import com.payflow.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByInvoiceId(Long invoiceId);

    Optional<Payment> findByPaypalOrderId(String paypalOrderId);

    List<Payment> findByStatus(String status);
}