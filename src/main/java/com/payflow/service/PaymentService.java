package com.payflow.service;

import com.payflow.dto.request.PaymentRequest;
import com.payflow.dto.response.PaymentResponse;
import com.payflow.entity.Invoice;
import com.payflow.entity.Payment;
import com.payflow.repository.InvoiceRepository;
import com.payflow.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            InvoiceRepository invoiceRepository) {

        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
    }

    public PaymentResponse createPayment(PaymentRequest request) {

        Payment payment = new Payment();

        payment.setInvoiceId(request.getInvoiceId());
        payment.setAmount(request.getAmount());
        payment.setCurrency(
                request.getCurrency() != null
                        ? request.getCurrency()
                        : "INR"
        );
        payment.setStatus("PENDING");
        payment.setPaypalOrderId(request.getPaypalOrderId());

        Payment savedPayment = paymentRepository.save(payment);

        return toResponse(savedPayment);
    }

    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PaymentResponse getPaymentById(Long id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Payment not found with id: " + id));

        return toResponse(payment);
    }

    public PaymentResponse getPaymentByInvoiceId(Long invoiceId) {

        Payment payment = paymentRepository.findByInvoiceId(invoiceId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Payment not found for invoice id: " + invoiceId));

        return toResponse(payment);
    }

    public PaymentResponse markAsPaid(Long id, String paypalPaymentId) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Payment not found with id: " + id));

        payment.setStatus("COMPLETED");
        payment.setPaypalPaymentId(paypalPaymentId);
        payment.setPaidAt(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);

        Invoice invoice = invoiceRepository.findById(payment.getInvoiceId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Invoice not found with id: " + payment.getInvoiceId()));

        invoice.setStatus("PAID");
        invoice.setPaidAt(LocalDateTime.now());

        invoiceRepository.save(invoice);

        return toResponse(savedPayment);
    }

    private PaymentResponse toResponse(Payment payment) {

        return new PaymentResponse(
                payment.getId(),
                payment.getInvoiceId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getPaypalOrderId(),
                payment.getPaypalPaymentId(),
                payment.getPaidAt(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }
}