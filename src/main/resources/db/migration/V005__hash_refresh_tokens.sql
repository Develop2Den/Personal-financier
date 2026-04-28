DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'refresh_tokens'
          AND column_name = 'token'
    ) THEN
        ALTER TABLE refresh_tokens RENAME COLUMN token TO token_hash;
    END IF;
END $$;

ALTER TABLE refresh_tokens
    ALTER COLUMN token_hash TYPE VARCHAR(64);
