CREATE TABLE IF NOT EXISTS refresh_tokens
(
    id          BIGSERIAL PRIMARY KEY,
    token       VARCHAR(255) UNIQUE NOT NULL,
    user_id     BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    expiry_date TIMESTAMP NOT NULL,
    revoked_at  TIMESTAMP,
    last_used_at TIMESTAMP,
    created_at  TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS password_reset_tokens
(
    id          BIGSERIAL PRIMARY KEY,
    token       VARCHAR(255) UNIQUE NOT NULL,
    user_id     BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    expiry_date TIMESTAMP NOT NULL,
    used_at     TIMESTAMP,
    created_at  TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS audit_logs
(
    id            BIGSERIAL PRIMARY KEY,
    event_type    VARCHAR(50) NOT NULL,
    status        VARCHAR(20) NOT NULL,
    user_id       BIGINT REFERENCES users(id) ON DELETE SET NULL,
    principal     VARCHAR(100),
    ip_address    VARCHAR(64),
    user_agent    VARCHAR(255),
    details       VARCHAR(500),
    created_at    TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user
    ON refresh_tokens(user_id);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_expiry
    ON refresh_tokens(expiry_date);

CREATE INDEX IF NOT EXISTS idx_password_reset_tokens_user
    ON password_reset_tokens(user_id);

CREATE INDEX IF NOT EXISTS idx_password_reset_tokens_expiry
    ON password_reset_tokens(expiry_date);

CREATE INDEX IF NOT EXISTS idx_audit_logs_created_at
    ON audit_logs(created_at);

CREATE INDEX IF NOT EXISTS idx_audit_logs_user
    ON audit_logs(user_id);
