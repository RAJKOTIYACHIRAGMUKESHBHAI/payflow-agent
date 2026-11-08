package com.payflow.service.impl;

import com.payflow.dto.request.AiDraftRequest;
import com.payflow.dto.request.InvoiceRequest;
import com.payflow.dto.request.ReminderSendRequest;
import com.payflow.dto.response.AgentRecoveryResponse;
import com.payflow.dto.response.AiDraftResponse;
import com.payflow.dto.response.InvoiceResponse;
import com.payflow.dto.response.ReminderSendResponse;
import com.payflow.entity.Client;
import com.payflow.entity.Invoice;
import com.payflow.entity.MerchantSettings;
import com.payflow.repository.ClientRepository;
import com.payflow.repository.InvoiceRepository;
import com.payflow.repository.MerchantSettingsRepository;
import com.payflow.service.AgentService;
import com.payflow.service.AuditLogService;
import com.payflow.service.InvoiceService;
import com.payflow.service.PayPalService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AgentServiceImpl implements AgentService {

    private final ClientRepository clientRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceService invoiceService;
    private final PayPalService paypalService;
    private final AuditLogService auditLogService;
    private final MerchantSettingsRepository merchantSettingsRepository;

    public AgentServiceImpl(ClientRepository clientRepository,
                            InvoiceRepository invoiceRepository,
                            InvoiceService invoiceService,
                            PayPalService paypalService,
                            AuditLogService auditLogService,
                            MerchantSettingsRepository merchantSettingsRepository) {
        this.clientRepository = clientRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoiceService = invoiceService;
        this.paypalService = paypalService;
        this.auditLogService = auditLogService;
        this.merchantSettingsRepository = merchantSettingsRepository;
    }

    @Override
    public AiDraftResponse draftInvoice(AiDraftRequest request) {
        String prompt = request.getPrompt();
        AiDraftResponse response = new AiDraftResponse();
        List<String> missingFields = new ArrayList<>();

        Optional<Client> foundClient = clientRepository.findAll().stream()
                .filter(c -> prompt.toLowerCase().contains(c.getName().toLowerCase()))
                .findFirst();

        if (foundClient.isPresent()) {
            Client client = foundClient.get();
            response.setClientName(client.getName());
            response.setClientEmail(client.getEmail());
        } else {
            Matcher nameMatcher = Pattern.compile("client\\s+([A-Za-z0-9\\s]+?)(?=\\s+for|\\s+due|$)").matcher(prompt);
            if (nameMatcher.find()) {
                response.setClientName(nameMatcher.group(1).trim());
            } else {
                response.setClientName("Unknown Client");
            }
            missingFields.add("Client record not found for '" + response.getClientName() + "'");
        }

        Matcher amountMatcher = Pattern.compile("\\$(\\d+(\\.\\d{1,2})?)").matcher(prompt);
        if (amountMatcher.find()) {
            response.setAmount(new BigDecimal(amountMatcher.group(1)));
        } else {
            response.setAmount(BigDecimal.ZERO);
            missingFields.add("Invoice amount could not be parsed");
        }

        response.setCurrency("USD");

        Matcher daysMatcher = Pattern.compile("due in (\\d+) days").matcher(prompt);
        if (daysMatcher.find()) {
            int days = Integer.parseInt(daysMatcher.group(1));
            response.setDueDate(LocalDate.now().plusDays(days));
        } else {
            response.setDueDate(LocalDate.now().plusDays(14));
        }

        response.setDescription("AI Generated: " + prompt);
        response.setMissingOrAmbiguousFields(missingFields);
        response.setReadyForApproval(missingFields.isEmpty());

        return response;
    }

    @Override
    public List<AgentRecoveryResponse> analyzePortfolio() {
        List<Invoice> invoices = invoiceRepository.findAll();
        List<AgentRecoveryResponse> result = new ArrayList<>();

        for (Invoice invoice : invoices) {
            if ("PAID".equalsIgnoreCase(invoice.getStatus()) || "CANCELLED".equalsIgnoreCase(invoice.getStatus())) {
                continue;
            }

            Client client = clientRepository.findById(invoice.getClientId()).orElse(null);
            String clientName = (client != null) ? client.getName() : "Unknown Client";
            String clientEmail = (client != null) ? client.getEmail() : "";

            long daysOverdue = ChronoUnit.DAYS.between(invoice.getDueDate(), LocalDate.now());
            AgentRecoveryResponse item = new AgentRecoveryResponse();

            item.setInvoiceId(invoice.getId());
            item.setInvoiceNumber(invoice.getInvoiceNumber());
            item.setClientName(clientName);
            item.setClientEmail(clientEmail);
            item.setAmount(invoice.getAmount());
            item.setCurrency(invoice.getCurrency());
            item.setDueDate(invoice.getDueDate());
            item.setDaysOverdue(daysOverdue);

            if (daysOverdue < 0) {
                item.setUrgencyLevel("UPCOMING");
                item.setRecommendedStrategy("Friendly reminder before due date");
                item.setGeneratedEmailSubject("Friendly Reminder: Upcoming Invoice " + invoice.getInvoiceNumber());
                item.setGeneratedEmailBody("Dear " + clientName + ",\n\n" +
                        "This is a quick reminder that invoice " + invoice.getInvoiceNumber() +
                        " for " + invoice.getCurrency() + " " + invoice.getAmount() + " is due on " + invoice.getDueDate() + ".\n\n" +
                        "Please let us know if you have any questions regarding payment.");
            } else if (daysOverdue <= 30) {
                item.setUrgencyLevel("MEDIUM");
                item.setRecommendedStrategy("Direct urgent request with direct PayPal checkout option");
                item.setGeneratedEmailSubject("URGENT: Outstanding Payment for Invoice " + invoice.getInvoiceNumber());
                item.setGeneratedEmailBody("Dear " + clientName + ",\n\n" +
                        "Invoice " + invoice.getInvoiceNumber() + " is currently " + daysOverdue + " days overdue. Total outstanding amount is " +
                        invoice.getCurrency() + " " + invoice.getAmount() + ".\n\n" +
                        "Please click the instant PayPal link below to complete your payment now and avoid account suspension.");
            } else {
                item.setUrgencyLevel("HIGH");
                item.setRecommendedStrategy("Final warning notice before collection escalation");
                item.setGeneratedEmailSubject("FINAL NOTICE: Immediate Action Required - Invoice " + invoice.getInvoiceNumber());
                item.setGeneratedEmailBody("Dear " + clientName + ",\n\n" +
                        "Invoice " + invoice.getInvoiceNumber() + " is severely overdue by " + daysOverdue + " days. Amount due: " +
                        invoice.getCurrency() + " " + invoice.getAmount() + ".\n\n" +
                        "Immediate payment is required. Please settle this balance immediately via PayPal to clear your account.");
            }

            if (invoice.getPaypalPaymentLink() != null && !invoice.getPaypalPaymentLink().isEmpty()) {
                item.setPaypalPaymentLink(invoice.getPaypalPaymentLink());
            } else {
                try {
                    String paypalOrderId = paypalService.createOrder(invoice.getAmount(), invoice.getCurrency());
                    String paymentLink = "https://www.sandbox.paypal.com/checkoutnow?token=" + paypalOrderId;
                    invoice.setPaypalOrderId(paypalOrderId);
                    invoice.setPaypalPaymentLink(paymentLink);
                    invoiceRepository.save(invoice);
                    item.setPaypalPaymentLink(paymentLink);
                } catch (Exception e) {
                    item.setPaypalPaymentLink("https://www.sandbox.paypal.com/checkoutnow?token=MOCK-" + invoice.getId());
                }
            }

            result.add(item);
        }

        return result;
    }

    @Override
    public InvoiceResponse approveDraft(AiDraftResponse draft) {
        Client client = clientRepository.findAll().stream()
                .filter(c -> c.getName().equalsIgnoreCase(draft.getClientName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Client not found for name: " + draft.getClientName()));

        InvoiceRequest request = new InvoiceRequest();
        request.setClientId(client.getId());
        request.setInvoiceNumber("INV-" + System.currentTimeMillis() % 100000);
        request.setAmount(draft.getAmount());
        request.setCurrency(draft.getCurrency());
        request.setDueDate(draft.getDueDate());
        request.setDescription(draft.getDescription());

        InvoiceResponse response = invoiceService.createInvoice(request);

        auditLogService.logAction(
                "AGENT",
                "CREATE_INVOICE_FROM_DRAFT",
                "INVOICE",
                response.getId(),
                "Approved AI draft for " + draft.getClientName() + " (" + response.getInvoiceNumber() + ")",
                "SUCCESS"
        );

        return response;
    }

    @Override
    public ReminderSendResponse sendReminder(ReminderSendRequest request) {
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + request.getInvoiceId()));

        if ("PAID".equalsIgnoreCase(invoice.getStatus()) || "CANCELLED".equalsIgnoreCase(invoice.getStatus())) {
            return new ReminderSendResponse(
                    invoice.getId(),
                    "REJECTED_INELIGIBLE",
                    "Cannot send reminder for invoice with status " + invoice.getStatus(),
                    invoice.getReminderCount(),
                    invoice.getLastReminderSentAt()
            );
        }

        MerchantSettings settings = merchantSettingsRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> new MerchantSettings(null, "PayFlow", "merchant@payflow.com", "USD", "UTC", 3, 3));

        if (invoice.getReminderCount() >= settings.getMaxReminders()) {
            return new ReminderSendResponse(
                    invoice.getId(),
                    "REJECTED_MAX_LIMIT",
                    "Max reminder limit reached (" + settings.getMaxReminders() + ")",
                    invoice.getReminderCount(),
                    invoice.getLastReminderSentAt()
            );
        }

        if (invoice.getLastReminderSentAt() != null) {
            long daysSinceLast = Duration.between(invoice.getLastReminderSentAt(), LocalDateTime.now()).toDays();
            if (daysSinceLast < settings.getCooldownDays()) {
                return new ReminderSendResponse(
                        invoice.getId(),
                        "REJECTED_COOLDOWN",
                        "Cooldown active. Must wait " + settings.getCooldownDays() + " days between reminders.",
                        invoice.getReminderCount(),
                        invoice.getLastReminderSentAt()
                );
            }
        }

        invoice.setReminderCount(invoice.getReminderCount() + 1);
        invoice.setLastReminderSentAt(LocalDateTime.now());
        invoiceRepository.save(invoice);

        auditLogService.logAction(
                "AGENT",
                "SEND_REMINDER",
                "INVOICE",
                invoice.getId(),
                "Sent reminder #" + invoice.getReminderCount() + " via " + request.getChannel(),
                "SUCCESS"
        );

        return new ReminderSendResponse(
                invoice.getId(),
                "SUCCESS",
                "Reminder #" + invoice.getReminderCount() + " delivered via " + request.getChannel(),
                invoice.getReminderCount(),
                invoice.getLastReminderSentAt()
        );
    }
}
