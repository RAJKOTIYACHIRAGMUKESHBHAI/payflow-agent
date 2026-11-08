CREATE TABLE clients (
                         id BIGINT NOT NULL AUTO_INCREMENT,
                         full_name VARCHAR(150) NOT NULL,
                         email VARCHAR(150) NOT NULL,
                         phone VARCHAR(30),
                         company_name VARCHAR(150),
                         industry VARCHAR(100),
                         preferred_reminder_channel VARCHAR(20) NOT NULL DEFAULT 'EMAIL',

                         average_payment_delay_days DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
                         on_time_payment_percentage DECIMAL(5, 2) NOT NULL DEFAULT 0.00,
                         payment_probability DECIMAL(5, 2) NOT NULL DEFAULT 50.00,
                         risk_level VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',

                         is_active BOOLEAN NOT NULL DEFAULT TRUE,

                         created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                         CONSTRAINT pk_clients PRIMARY KEY (id),
                         CONSTRAINT uk_clients_email UNIQUE (email)
);

CREATE INDEX idx_clients_risk_level ON clients(risk_level);
CREATE INDEX idx_clients_active ON clients(is_active);