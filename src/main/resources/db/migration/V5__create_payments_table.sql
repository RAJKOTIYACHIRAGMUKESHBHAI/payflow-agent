CREATE TABLE payments (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          invoice_id BIGINT NOT NULL,
                          amount DECIMAL(12, 2) NOT NULL,
                          currency VARCHAR(10) NOT NULL,
                          status VARCHAR(30) NOT NULL,
                          paypal_order_id VARCHAR(255),
                          paypal_payment_id VARCHAR(255),
                          paid_at DATETIME,
                          created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                          CONSTRAINT pk_payments PRIMARY KEY (id),
                          CONSTRAINT fk_payments_invoice
                              FOREIGN KEY (invoice_id) REFERENCES invoices(id)
);

CREATE INDEX idx_payments_invoice_id ON payments(invoice_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_paypal_order_id ON payments(paypal_order_id);