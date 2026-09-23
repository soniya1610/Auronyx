-- V6: Module 2 - Recycling Records & Traceability (QR) Records

CREATE TABLE IF NOT EXISTS recycling_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_id BIGINT NOT NULL,
    pickup_id BIGINT NOT NULL,
    waste_category_id BIGINT NOT NULL,
    waste_item_id BIGINT NULL,
    weight DECIMAL(8, 3) NOT NULL,
    collector_id BIGINT NOT NULL,
    recycler_id BIGINT NULL,
    handover_info TEXT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'COLLECTED',
    processing_info TEXT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_recycling_txn FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE CASCADE,
    CONSTRAINT fk_recycling_pickup FOREIGN KEY (pickup_id) REFERENCES pickups(id) ON DELETE CASCADE,
    CONSTRAINT fk_recycling_category FOREIGN KEY (waste_category_id) REFERENCES waste_categories(id),
    CONSTRAINT fk_recycling_item FOREIGN KEY (waste_item_id) REFERENCES waste_items(id) ON DELETE SET NULL,
    CONSTRAINT fk_recycling_collector FOREIGN KEY (collector_id) REFERENCES collectors(id) ON DELETE CASCADE,
    CONSTRAINT fk_recycling_recycler FOREIGN KEY (recycler_id) REFERENCES recyclers(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS traceability_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    qr_code VARCHAR(150) NOT NULL UNIQUE,
    transaction_id BIGINT NOT NULL,
    recycling_record_id BIGINT NULL,
    payload_json TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_trace_txn FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE CASCADE,
    CONSTRAINT fk_trace_recycling FOREIGN KEY (recycling_record_id) REFERENCES recycling_records(id) ON DELETE SET NULL
);

CREATE INDEX idx_recycling_status ON recycling_records(status);
CREATE INDEX idx_recycling_recycler ON recycling_records(recycler_id);
CREATE INDEX idx_trace_qr ON traceability_records(qr_code);
CREATE INDEX idx_trace_txn ON traceability_records(transaction_id);
