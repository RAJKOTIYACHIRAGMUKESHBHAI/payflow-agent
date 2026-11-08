CREATE TABLE invoices (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          client_id BIGINT NOT NULL,
                          invoice_number VARCHAR(100) NOT NULL,
                          amount DECIMAL(12, 2) NOT NULL,
                          currency VARCHAR(10) NOT NULL,
                          issue_date DATE NOT NULL,
                          due_date DATE NOT NULL,
                          status VARCHAR(30) NOT NULL,
                          description VARCHAR(500),
                          paid_at DATETIME,
                          created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                          CONSTRAINT pk_invoices PRIMARY KEY (id),
                          CONSTRAINT uk_invoices_invoice_number UNIQUE (invoice_number),
                          CONSTRAINT fk_invoices_client
                              FOREIGN KEY (client_id) REFERENCES clients(id)
);

CREATE INDEX idx_invoices_client_id ON invoices(client_id);
CREATE INDEX idx_invoices_status ON invoices(status);
CREATE INDEX idx_invoices_due_date ON invoices(due_date);