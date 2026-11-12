# PayFlow Agent: Autonomous AI Invoicing & Payment Recovery

**Live Application:** [https://payflow-agent-one.vercel.app](https://payflow-agent-one.vercel.app)

PayFlow Agent is a full-stack AI accounts receivable and invoicing copilot that converts plain-English prompts into database-verified invoices, generates live **PayPal Sandbox** checkout links, analyzes overdue receivables, and drafts tailored payment reminders.

---

## How We Used PayPal & AI

### 1. PayPal Developer Platform (Sandbox Integration)
* **OAuth 2.0 & Orders v2 REST API:** When an invoice is approved or marked as sent, the Spring Boot backend authenticates with PayPal Sandbox and creates a live PayPal Order (`POST /v2/checkout/orders`).
* **Checkout Link Persistence:** The returned `PayPal Order ID` and `approve` checkout URL (`sandbox.paypal.com/checkoutnow`) are persisted in MySQL and embedded directly into invoice views and automated client reminder emails.

### 2. AI Invoicing & Payment Recovery Copilot
* **Natural-Language Invoice Drafting:** Converts unstructured instructions (e.g., *"Create an invoice of $400 for Acme Corp for API integration"*) into structured invoice DTOs (client, amount, currency, due date, description).
* **Database-Backed Verification Guardrails:** Cross-references every AI-extracted client against live MySQL client records before permitting approval, flagging missing or ambiguous fields to prevent billing hallucinations.
* **Overdue Analysis & Reminder Generation:** Evaluates invoice urgency (`UPCOMING` / `OVERDUE`), recommends follow-up strategies, and generates customized reminder emails with the live PayPal payment URL.
* **Immutable Audit Trail:** Logs every AI and merchant action (`AGENT` vs `MERCHANT`) in an audit ledger for full transparency.

---

## Tech Stack & Sponsor Integrations
* **Backend:** Java, Spring Boot, Spring Data JPA, REST APIs
* **Frontend:** React, Vite, JavaScript, CSS
* **Database:** MySQL hosted on **Aiven Cloud** (SSL-enforced)
* **Hosting & Sponsor Tools:**
  * **Render:** Cloud hosting for the containerized Spring Boot API (`Dockerfile`)
  * **Vercel:** Global edge hosting for the React/Vite frontend
  * **PayPal Developer Sandbox:** Live test payment order generation

---

## Local Setup Instructions

### Prerequisites
* Java 17+ & Maven
* Node.js 18+ & npm
* MySQL 8.0+
* PayPal Developer Sandbox Credentials

### 1. Backend Setup (Spring Boot)
```bash
# Clone the repository
git clone [https://github.com/RAJKOTIYACHIRAGMUKESHBHAI/payflow-agent.git](https://github.com/RAJKOTIYACHIRAGMUKESHBHAI/payflow-agent.git)
cd payflow-agent

# Set environment variables in your terminal or hosting provider
export DB_URL="jdbc:mysql://localhost:3306/payflow"
export DB_USERNAME="root"
export DB_PASSWORD="<YOUR_MYSQL_PASSWORD>"
export PAYPAL_CLIENT_ID="<YOUR_PAYPAL_SANDBOX_CLIENT_ID>"
export PAYPAL_CLIENT_SECRET="<YOUR_PAYPAL_SANDBOX_SECRET>"

# Run the Spring Boot API
./mvnw spring-boot:run
