-- V7: Module 3 Stub - EPR Compliance & Fraud Alert Tables

CREATE TABLE IF NOT EXISTS epr_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recycler_id BIGINT NULL,
    certificate_no VARCHAR(100) UNIQUE,
    waste_type VARCHAR(100),
    quantity_kg DECIMAL(10, 3),
    period_start DATE,
    period_end DATE,
    issued_by VARCHAR(200),
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_epr_recycler FOREIGN KEY (recycler_id) REFERENCES recyclers(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS fraud_alerts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pickup_id BIGINT NULL,
    user_id BIGINT NULL,
    alert_type VARCHAR(100) NOT NULL,
    description TEXT,
    severity VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    confidence_score DOUBLE,
    reviewed_by VARCHAR(200),
    review_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP NULL,
    CONSTRAINT fk_fraud_pickup FOREIGN KEY (pickup_id) REFERENCES pickups(id) ON DELETE SET NULL,
    CONSTRAINT fk_fraud_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_fraud_status ON fraud_alerts(status);
CREATE INDEX idx_fraud_user ON fraud_alerts(user_id);
CREATE INDEX idx_epr_recycler ON epr_records(recycler_id);
CREATE INDEX idx_epr_verified ON epr_records(verified);
