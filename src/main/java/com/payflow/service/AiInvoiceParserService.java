package com.payflow.service;

import com.payflow.dto.response.AiDraftResponse;
import com.payflow.entity.Client;
import com.payflow.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AiInvoiceParserService {

    private final ClientRepository clientRepository;

    public AiInvoiceParserService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public AiDraftResponse parseNaturalLanguagePrompt(String prompt) {
        List<String> missingOrAmbiguousFields = new ArrayList<>();

        if (prompt == null || prompt.isBlank()) {
            missingOrAmbiguousFields.add("Prompt text is empty");
            return new AiDraftResponse(null, null, null, null, "USD", LocalDate.now().plusDays(14), false, missingOrAmbiguousFields);
        }

        String lowerPrompt = prompt.toLowerCase();

        BigDecimal amount = null;
        String currency = "USD";

        Pattern currencyAmountPattern = Pattern.compile("(?i)(USD|EUR|GBP|INR|CAD|AUD|\\$|\\u20AC|\\u00A3|\\u20B9)?\\s*([0-9]+(?:\\.[0-9]{1,2})?)");
        Matcher amountMatcher = currencyAmountPattern.matcher(prompt);

        if (amountMatcher.find()) {
            String symbolOrCode = amountMatcher.group(1);
            String numericStr = amountMatcher.group(2);

            if (numericStr != null && !numericStr.isBlank()) {
                amount = new BigDecimal(numericStr);
            }

            if (symbolOrCode != null) {
                switch (symbolOrCode.toUpperCase()) {
                    case "$":
                    case "USD":
                        currency = "USD";
                        break;
                    case "\u20AC":
                    case "EUR":
                        currency = "EUR";
                        break;
                    case "\u00A3":
                    case "GBP":
                        currency = "GBP";
                        break;
                    case "\u20B9":
                    case "INR":
                        currency = "INR";
                        break;
                    default:
                        currency = symbolOrCode.toUpperCase();
                        break;
                }
            }
        }

        if (amount == null) {
            missingOrAmbiguousFields.add("Amount");
        }

        LocalDate dueDate;
        Pattern dueDaysPattern = Pattern.compile("due\\s+in\\s+([0-9]+)\\s+days", Pattern.CASE_INSENSITIVE);
        Matcher dueMatcher = dueDaysPattern.matcher(prompt);

        if (dueMatcher.find()) {
            int days = Integer.parseInt(dueMatcher.group(1));
            dueDate = LocalDate.now().plusDays(days);
        } else {
            dueDate = LocalDate.now().plusDays(14);
            missingOrAmbiguousFields.add("Due date not explicitly specified (defaulted to 14 days)");
        }

        List<Client> clients = clientRepository.findAll();
        Client matchedClient = null;

        for (Client client : clients) {
            if (client.getName() != null && lowerPrompt.contains(client.getName().toLowerCase())) {
                matchedClient = client;
                break;
            }
        }

        String clientName = matchedClient != null ? matchedClient.getName() : null;
        String clientEmail = matchedClient != null ? matchedClient.getEmail() : null;

        if (matchedClient == null) {
            Pattern clientPattern = Pattern.compile("for\\s+([A-Za-z0-9]+)", Pattern.CASE_INSENSITIVE);
            Matcher clientMatcher = clientPattern.matcher(prompt);
            if (clientMatcher.find()) {
                clientName = clientMatcher.group(1);
            }
            missingOrAmbiguousFields.add("Client record not found for '" + (clientName != null ? clientName : "specified target") + "'");
        }

        String description = "AI Generated: " + prompt;
        boolean readyForApproval = (matchedClient != null) && (amount != null);

        return new AiDraftResponse(
                clientName,
                clientEmail,
                description,
                amount,
                currency,
                dueDate,
                readyForApproval,
                missingOrAmbiguousFields
        );
    }
}
