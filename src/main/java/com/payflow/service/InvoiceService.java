package com.payflow.service;

import com.payflow.dto.request.InvoiceRequest;
import com.payflow.dto.response.InvoiceResponse;
import com.payflow.entity.Invoice;
import com.payflow.repository.ClientRepository;
import com.payflow.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ClientRepository clientRepository;
    private final AuditLogService auditLogService;

    public InvoiceService(InvoiceRepository invoiceRepository,
                          ClientRepository clientRepository,
                          AuditLogService auditLogService) {
        this.invoiceRepository = invoiceRepository;
        this.clientRepository = clientRepository;
        this.auditLogService = auditLogService;
    }

    public InvoiceResponse createInvoice(InvoiceRequest request) {
        if (request.getClientId() != null && !clientRepository.existsById(request.getClientId())) {
            throw new RuntimeException("Client not found with ID: " + request.getClientId());
        }

        Invoice invoice = new Invoice();
        invoice.setClientId(request.getClientId());
        invoice.setInvoiceNumber(request.getInvoiceNumber());
        invoice.setAmount(request.getAmount());
        invoice.setCurrency(request.getCurrency() != null ? request.getCurrency() : "USD");
        invoice.setIssueDate(request.getIssueDate() != null ? request.getIssueDate() : LocalDate.now());
        invoice.setDueDate(request.getDueDate());
        invoice.setStatus(request.getStatus() != null && !request.getStatus().isBlank() ? request.getStatus() : "DRAFT");
        invoice.setDescription(request.getDescription());

        Invoice saved = invoiceRepository.save(invoice);

        auditLogService.logAction(
            "MERCHANT",
            "CREATE_INVOICE",
            "INVOICE",
            saved.getId(),
            "Created invoice " + saved.getInvoiceNumber() + " for amount " + saved.getCurrency() + " " + saved.getAmount(),
            "SUCCESS"
        );

        return mapToResponse(saved);
    }

    public Invoice createInvoice(Invoice invoice) {
        if (invoice.getIssueDate() == null) {
            invoice.setIssueDate(LocalDate.now());
        }
        if (invoice.getStatus() == null || invoice.getStatus().isBlank()) {
            invoice.setStatus("DRAFT");
        }
        Invoice saved = invoiceRepository.save(invoice);

        auditLogService.logAction(
            "MERCHANT",
            "CREATE_INVOICE",
            "INVOICE",
            saved.getId(),
            "Created invoice " + saved.getInvoiceNumber() + " for amount " + saved.getCurrency() + " " + saved.getAmount(),
            "SUCCESS"
        );

        return saved;
    }

    public List<InvoiceResponse> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public InvoiceResponse getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + id));
        return mapToResponse(invoice);
    }

    public List<InvoiceResponse> getInvoicesByClientId(Long clientId) {
        return invoiceRepository.findByClientId(clientId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public InvoiceResponse updateInvoiceStatus(Long id, String status) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + id));

        String oldStatus = invoice.getStatus();
        invoice.setStatus(status);
        Invoice updated = invoiceRepository.save(invoice);

        auditLogService.logAction(
            "MERCHANT",
            "UPDATE_INVOICE_STATUS",
            "INVOICE",
            updated.getId(),
            "Invoice status changed from " + oldStatus + " to " + status,
            "SUCCESS"
        );

        return mapToResponse(updated);
    }

    private InvoiceResponse mapToResponse(Invoice invoice) {
        InvoiceResponse response = new InvoiceResponse();
        response.setId(invoice.getId());
        response.setInvoiceNumber(invoice.getInvoiceNumber());
        response.setClientId(invoice.getClientId());

        if (invoice.getClientId() != null) {
            clientRepository.findById(invoice.getClientId()).ifPresent(client -> {
                response.setClientName(client.getName());
                response.setClientEmail(client.getEmail());
            });
        }

        response.setAmount(invoice.getAmount());
        response.setCurrency(invoice.getCurrency());
        response.setIssueDate(invoice.getIssueDate());
        response.setDueDate(invoice.getDueDate());
        response.setStatus(invoice.getStatus());
        response.setDescription(invoice.getDescription());
        response.setPaypalOrderId(invoice.getPaypalOrderId());
        response.setPaypalPaymentLink(invoice.getPaypalPaymentLink());
        return response;
    }
}
