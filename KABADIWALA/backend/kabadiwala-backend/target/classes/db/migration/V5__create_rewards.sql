-- V5: Module 3 Stub - Rewards, Gamification & Points Tables
-- (Full rewards logic implemented in Module 3: Rewards & Ecosystem Module)

CREATE TABLE IF NOT EXISTS rewards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    type VARCHAR(50) NOT NULL,
    points_cost INT NOT NULL DEFAULT 0,
    cash_value DECIMAL(10, 2) NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    stock_limit INT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS badges (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    icon_url VARCHAR(500),
    points_required INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS challenges (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    target_quantity INT,
    reward_points INT NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    start_date TIMESTAMP NULL,
    end_date TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS point_ledger (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,
    points INT NOT NULL,
    description VARCHAR(255),
    reference_id VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_points_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS redemptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    reward_id BIGINT NULL,
    points_used INT NOT NULL,
    cash_value DECIMAL(10, 2) NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    reference_no VARCHAR(100) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_redemption_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_redemption_reward FOREIGN KEY (reward_id) REFERENCES rewards(id) ON DELETE SET NULL
);

CREATE INDEX idx_points_user ON point_ledger(user_id);
CREATE INDEX idx_points_type ON point_ledger(type);
CREATE INDEX idx_redemption_user ON redemptions(user_id);
CREATE INDEX idx_redemption_status ON redemptions(status);

-- Seed sample rewards catalog
INSERT IGNORE INTO rewards (id, title, description, type, points_cost, cash_value, active) VALUES
(1, 'Cash Bonus ₹10', 'Redeem 100 points for ₹10 wallet credit', 'CASH_BACK', 100, 10.00, TRUE),
(2, 'Cash Bonus ₹50', 'Redeem 500 points for ₹50 wallet credit', 'CASH_BACK', 500, 50.00, TRUE),
(3, 'Green Warrior Badge', 'Awarded to committed recyclers', 'BADGE', 200, NULL, TRUE);
