-- V3: Module 2 - Pickups, Status History & Waste Verifications

CREATE TABLE IF NOT EXISTS pickups (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    waste_category_id BIGINT NOT NULL,
    waste_item_id BIGINT NULL,
    estimated_quantity INT DEFAULT 1,
    estimated_weight DECIMAL(8, 3) NULL,
    image_url VARCHAR(255) NULL,
    address_line VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    pincode VARCHAR(20) NOT NULL,
    latitude DOUBLE NULL,
    longitude DOUBLE NULL,
    scheduled_date DATE NOT NULL,
    scheduled_time VARCHAR(50) NOT NULL,
    notes TEXT NULL,
    assigned_collector_id BIGINT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'REQUESTED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_pickup_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_pickup_category FOREIGN KEY (waste_category_id) REFERENCES waste_categories(id),
    CONSTRAINT fk_pickup_item FOREIGN KEY (waste_item_id) REFERENCES waste_items(id) ON DELETE SET NULL,
    CONSTRAINT fk_pickup_collector FOREIGN KEY (assigned_collector_id) REFERENCES collectors(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS pickup_status_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pickup_id BIGINT NOT NULL,
    old_status VARCHAR(50) NULL,
    new_status VARCHAR(50) NOT NULL,
    changed_by_user_id BIGINT NOT NULL,
    remarks VARCHAR(255) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_history_pickup FOREIGN KEY (pickup_id) REFERENCES pickups(id) ON DELETE CASCADE,
    CONSTRAINT fk_history_user FOREIGN KEY (changed_by_user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS waste_verifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pickup_id BIGINT NOT NULL UNIQUE,
    collector_id BIGINT NOT NULL,
    actual_category_id BIGINT NOT NULL,
    actual_waste_item_id BIGINT NULL,
    actual_weight DECIMAL(8, 3) NOT NULL,
    item_condition VARCHAR(50) NULL,
    notes TEXT NULL,
    verified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_verification_pickup FOREIGN KEY (pickup_id) REFERENCES pickups(id) ON DELETE CASCADE,
    CONSTRAINT fk_verification_collector FOREIGN KEY (collector_id) REFERENCES collectors(id) ON DELETE CASCADE,
    CONSTRAINT fk_verification_category FOREIGN KEY (actual_category_id) REFERENCES waste_categories(id),
    CONSTRAINT fk_verification_item FOREIGN KEY (actual_waste_item_id) REFERENCES waste_items(id) ON DELETE SET NULL
);

CREATE INDEX idx_pickup_user ON pickups(user_id);
CREATE INDEX idx_pickup_collector ON pickups(assigned_collector_id);
CREATE INDEX idx_pickup_status ON pickups(status);
CREATE INDEX idx_pickup_scheduled ON pickups(scheduled_date);
