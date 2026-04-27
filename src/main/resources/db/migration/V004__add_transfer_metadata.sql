ALTER TABLE transactions
    ADD COLUMN IF NOT EXISTS transfer_reference VARCHAR(36);

ALTER TABLE transactions
    ADD COLUMN IF NOT EXISTS transfer_direction VARCHAR(20);

CREATE INDEX IF NOT EXISTS idx_transactions_transfer_reference
    ON transactions(transfer_reference);
