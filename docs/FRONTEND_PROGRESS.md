# Frontend Progress

## Completed
- Milestone A: shell layout, routing scaffold, API fetch wrapper, and live dashboard summary with currency selector.
- Added loading/error/empty states for dashboard.
- Added reusable `StatusBadge` and `ConfirmDialog` components.
- Added "PayPal Sandbox — test transactions" banner in layout.
- Milestone B: clients list/search and create/edit form wired to `/api/clients`.
- Milestone B: invoices list with status/currency frontend filters, create form wired to `/api/invoices`, and detail view via `/api/invoices/{id}`.
- Invoice detail page shows backend-provided PayPal payment URL and disables reconcile/refresh action because endpoint is not available.
- Milestone C: AI draft generate (`/api/agent/draft-invoice`) and explicit approve (`/api/agent/approve-draft`) with created invoice/payment-link display.
- Milestone C: overdue analysis (`/api/agent/analyze`) with reminder preview and explicit send confirmation (`/api/agent/send-reminder`).
- Milestone C: surfaced reminder rejection states (including cooldown/max-limit style backend statuses) from real `ReminderSendResponse`.
- Milestone D: merchant settings view/edit wired to `/api/merchant/settings`.
- Milestone D: read-only audit log table wired to `/api/audit-logs`.
- Final checks run on frontend (lint + production build).

## Pending
- None for requested milestones (A-D) under current backend contract.
