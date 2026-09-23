-- V2: Module 2 - Waste Categories, Items, Pricing & AI Predictions

CREATE TABLE IF NOT EXISTS waste_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    unit VARCHAR(20) NOT NULL DEFAULT 'KG',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS waste_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_waste_item_category FOREIGN KEY (category_id) REFERENCES waste_categories(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS waste_pricing (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL,
    rate_per_kg DECIMAL(10, 2) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    effective_from TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    effective_to TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_pricing_category FOREIGN KEY (category_id) REFERENCES waste_categories(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ai_predictions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NULL,
    category VARCHAR(100) NOT NULL,
    item VARCHAR(150) NOT NULL,
    item_condition VARCHAR(50),
    estimated_weight DECIMAL(8, 3) NOT NULL,
    price_min DECIMAL(10, 2) NOT NULL,
    price_max DECIMAL(10, 2) NOT NULL,
    confidence DECIMAL(5, 4) NOT NULL,
    image_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ai_pred_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_waste_cat_name ON waste_categories(name);
CREATE INDEX idx_pricing_cat_active ON waste_pricing(category_id, active);

-- Seed initial categories and pricing
INSERT IGNORE INTO waste_categories (id, name, description, active, unit) VALUES
(1, 'E-Waste', 'Electronic waste including phones, chargers, computers, batteries', TRUE, 'KG'),
(2, 'Plastic', 'PET bottles, containers, polythene and rigid plastics', TRUE, 'KG'),
(3, 'Paper', 'Newspapers, office paper, magazines, books', TRUE, 'KG'),
(4, 'Metal', 'Iron, aluminum, copper, brass, scrap metals', TRUE, 'KG'),
(5, 'Glass', 'Glass bottles, jars, unbroken clear and colored glass', TRUE, 'KG'),
(6, 'Cardboard', 'Corrugated boxes, packaging cartons', TRUE, 'KG'),
(7, 'Other', 'Mixed recyclable waste', TRUE, 'KG');

INSERT IGNORE INTO waste_pricing (id, category_id, rate_per_kg, active) VALUES
(1, 1, 250.00, TRUE),
(2, 2, 15.00, TRUE),
(3, 3, 12.00, TRUE),
(4, 4, 35.00, TRUE),
(5, 5, 5.00, TRUE),
(6, 6, 10.00, TRUE),
(7, 7, 8.00, TRUE);

INSERT IGNORE INTO waste_items (id, category_id, name, description, active) VALUES
(1, 1, 'Mobile Phone', 'Old smartphones and keypad phones', TRUE),
(2, 1, 'Laptop', 'Laptops, notebooks, netbooks', TRUE),
(3, 2, 'Plastic Bottles', 'Clear PET plastic bottles', TRUE),
(4, 3, 'Newspaper', 'Daily newspapers and newsprint', TRUE),
(5, 4, 'Copper Wire', 'Stripped or insulated copper wire', TRUE),
(6, 6, 'Carton Boxes', 'Heavy corrugated shipping boxes', TRUE);
