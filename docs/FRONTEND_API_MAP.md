| feature | method | endpoint | request | response | missing capability |
|---|---|---|---|---|---|
| Dashboard summary | GET | `/api/dashboard/summary?currency={code}` | Query: `currency` (optional, default `USD`) | `DashboardSummaryResponse { currency, totalOutstandingAmount, totalOverdueAmount, totalPaidAmount, totalInvoices, draftCount, sentCount, paidCount, overdueCount }` | None in contract |
| Clients: list | GET | `/api/clients` | None | `ClientResponse[] { id, name, email, phone, address }` | No server-side search endpoint (frontend filter only) |
| Clients: create | POST | `/api/clients` | `ClientRequest { name*, email*, phone, address }` | `201 ClientResponse` | Duplicate email does **not** return an error; service returns existing client record with 201 |
| Clients: detail | GET | `/api/clients/{id}` | Path: `id` | `ClientResponse` | None in contract |
| Clients: update | PUT | `/api/clients/{id}` | Path: `id`, body `ClientRequest` | `ClientResponse` | No explicit duplicate-email validation response contract |
| Invoices: list | GET | `/api/invoices` | None | `InvoiceResponse[] { id, invoiceNumber, clientId, clientName, clientEmail, amount, currency, issueDate, dueDate, status, description, paypalOrderId, paypalPaymentLink }` | No backend filter params for status/currency (frontend filter only) |
| Invoices: create | POST | `/api/invoices` | `InvoiceRequest { invoiceNumber*, clientId*, amount*, currency, issueDate, dueDate*, status, description }` | `201 InvoiceResponse` | No dedicated endpoint to generate/refresh PayPal link for an invoice |
| Invoices: detail | GET | `/api/invoices/{id}` | Path: `id` | `InvoiceResponse` | None in contract |
| Invoices by client | GET | `/api/invoices/client/{clientId}` | Path: `clientId` | `InvoiceResponse[]` | None in contract |
| Invoice status update | PUT | `/api/invoices/{id}/status?status={value}` | Path: `id`, query `status` | `InvoiceResponse` | No status enum contract exposed |
| AI draft: generate | POST | `/api/agent/draft-invoice` | `AiDraftRequest { prompt* }` | `AiDraftResponse { clientName, clientEmail, description, amount, currency, dueDate, readyForApproval, missingOrAmbiguousFields[] }` | None in contract |
| AI draft: approve | POST | `/api/agent/approve-draft` | Body: full `AiDraftResponse` | `201 InvoiceResponse` | Requires existing client name match; no endpoint to create missing client in same flow |
| Overdue analysis | GET | `/api/agent/analyze` | None | `AgentRecoveryResponse[] { invoiceId, invoiceNumber, clientName, clientEmail, amount, currency, dueDate, daysOverdue, urgencyLevel, recommendedStrategy, generatedEmailSubject, generatedEmailBody, paypalPaymentLink }` | Service can emit mock PayPal URL on PayPal failure (not guaranteed genuine) |
| Reminder send | POST | `/api/agent/send-reminder` | `ReminderSendRequest { invoiceId*, channel*, recipientEmail, emailSubject, emailBody }` | `ReminderSendResponse { invoiceId, status, message, currentReminderCount, sentAt }` | No queued/delivered lifecycle states; only immediate response statuses (`SUCCESS`, `REJECTED_*`) |
| Merchant settings: read | GET | `/api/merchant/settings` | None | `MerchantSettingsResponse { id, businessName, email, defaultCurrency, timezone, maxReminders, cooldownDays }` | None in contract |
| Merchant settings: update | PUT | `/api/merchant/settings` | `MerchantSettingsRequest { businessName*, email*, defaultCurrency*, timezone*, maxReminders*, cooldownDays* }` | `MerchantSettingsResponse` | None in contract |
| Audit logs list | GET | `/api/audit-logs` | None | `AuditLog[] { id, actor, action, targetType, targetId, details, outcome, timestamp }` | No pagination/filter query params |
| Audit logs by target | GET | `/api/audit-logs/{targetType}/{targetId}` | Path: `targetType`, `targetId` | `AuditLog[]` | No pagination |
| Payments: by invoice | GET | `/api/payments/invoice/{invoiceId}` | Path: `invoiceId` | `PaymentResponse { id, invoiceId, amount, currency, status, paypalOrderId, paypalPaymentId, paidAt, createdAt, updatedAt }` | No webhook/capture verification endpoint for checkout completion |
| Payments: mark complete | PUT | `/api/payments/{id}/complete` | Path: `id`, body `{ "paypalPaymentId": "..." }` | `PaymentResponse` (also marks linked invoice PAID) | Manual completion API; no proof-of-capture validation in contract |
| PayPal order create (optional utility) | POST | `/api/paypal/orders?amount={x}&currency={code}` | Query: `amount`, `currency` (default `INR`) | `{ status, orderId }` | Returns orderId only; no backend payment URL field in response |

**Auth and error format (observed):**
- No Spring Security/auth annotations or auth controller found; endpoints appear unauthenticated in current contract.
- No custom global error DTO/`@ControllerAdvice` found; frontend must handle Spring default error payloads plus validation failures from `@Valid`.
