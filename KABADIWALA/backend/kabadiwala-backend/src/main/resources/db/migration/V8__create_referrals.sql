-- V8: Module 3 Full Implementation — Referrals & User Referral Code

-- Add referral_code column to users (nullable, backward-compatible)
ALTER TABLE users ADD COLUMN IF NOT EXISTS referral_code VARCHAR(20) UNIQUE;

-- Referrals table
CREATE TABLE IF NOT EXISTS referrals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    referrer_id BIGINT NOT NULL,
    referred_id BIGINT NOT NULL UNIQUE,
    referral_code VARCHAR(20) NOT NULL,
    bonus_points_awarded INT NOT NULL DEFAULT 0,
    bonus_applied BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_referral_referrer FOREIGN KEY (referrer_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_referral_referred FOREIGN KEY (referred_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_referral_referrer ON referrals(referrer_id);
CREATE INDEX idx_referral_code    ON referrals(referral_code);
