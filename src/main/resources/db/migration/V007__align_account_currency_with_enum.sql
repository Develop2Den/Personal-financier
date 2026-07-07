DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM accounts
        WHERE currency IS NULL
           OR currency NOT IN ('UAH', 'USD', 'EUR')
    ) THEN
        RAISE EXCEPTION 'Cannot migrate accounts.currency: unsupported currency values exist';
    END IF;
END $$;

ALTER TABLE accounts
    ALTER COLUMN currency TYPE VARCHAR(3);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'chk_accounts_currency'
          AND conrelid = 'accounts'::regclass
    ) THEN
        ALTER TABLE accounts
            ADD CONSTRAINT chk_accounts_currency
                CHECK (currency IN ('UAH', 'USD', 'EUR'));
    END IF;
END $$;
