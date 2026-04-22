-- Таблица пользователей
CREATE TABLE IF NOT EXISTS users
(
    id         BIGSERIAL PRIMARY KEY,
    username   VARCHAR(50) UNIQUE NOT NULL,
    email      VARCHAR(100) UNIQUE NOT NULL,
    password   VARCHAR(255)        NOT NULL,
    role       VARCHAR(20)         NOT NULL DEFAULT 'USER',
    active     BOOLEAN             NOT NULL DEFAULT TRUE,
    verified   BOOLEAN             NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP           NOT NULL DEFAULT now(),
    updated_at TIMESTAMP           NOT NULL DEFAULT now()
);

-- Таблица категорий
CREATE TABLE IF NOT EXISTS categories
(
    id      BIGSERIAL PRIMARY KEY,
    name    VARCHAR(50) NOT NULL,
    type    VARCHAR(20) NOT NULL,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT unique_category_per_user UNIQUE (name, user_id)
);

-- Таблица счетов
CREATE TABLE IF NOT EXISTS accounts
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(100)  NOT NULL,
    currency   VARCHAR(10)   NOT NULL,
    balance    NUMERIC(12,2) NOT NULL DEFAULT 0,
    type       VARCHAR(20)   NOT NULL,
    user_id    BIGINT        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP     NOT NULL DEFAULT now(),
    updated_at TIMESTAMP     NOT NULL DEFAULT now()
);

-- Таблица транзакций
CREATE TABLE IF NOT EXISTS transactions
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    account_id  BIGINT        NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    category_id BIGINT        REFERENCES categories(id),
    amount      NUMERIC(12,2) NOT NULL,
    type        VARCHAR(20)   NOT NULL,
    description VARCHAR(255),
    date        TIMESTAMP     NOT NULL DEFAULT now(),
    created_at  TIMESTAMP     NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP     NOT NULL DEFAULT now()
);

-- Таблица бюджетов
CREATE TABLE IF NOT EXISTS budgets
(
    id           BIGSERIAL PRIMARY KEY,
    limit_amount NUMERIC(12,2) NOT NULL,
    start_date   DATE          NOT NULL,
    end_date     DATE          NOT NULL,
    period       VARCHAR(20)   NOT NULL,
    category_id  BIGINT REFERENCES categories(id),
    user_id      BIGINT REFERENCES users(id) ON DELETE CASCADE,
    created_at   TIMESTAMP     NOT NULL DEFAULT now(),
    updated_at   TIMESTAMP     NOT NULL DEFAULT now()
);

-- Таблица целей
CREATE TABLE IF NOT EXISTS goals
(
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(100)   NOT NULL,
    target_amount  NUMERIC(12,2)  NOT NULL,
    current_amount NUMERIC(12,2)  NOT NULL DEFAULT 0,
    deadline       DATE,
    status         VARCHAR(20)    NOT NULL DEFAULT 'ACTIVE',
    user_id        BIGINT         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at     TIMESTAMP      NOT NULL DEFAULT now(),
    updated_at     TIMESTAMP      NOT NULL DEFAULT now()
);

-- Таблица токенов подтверждения email
CREATE TABLE IF NOT EXISTS email_verification_tokens
(
    id          BIGSERIAL PRIMARY KEY,
    token       VARCHAR(255) UNIQUE NOT NULL,
    user_id     BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    expiry_date TIMESTAMP NOT NULL,
    is_live     BOOLEAN   NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now()
);

-- Индексы
CREATE INDEX IF NOT EXISTS idx_transactions_user
    ON transactions(user_id);

CREATE INDEX IF NOT EXISTS idx_transactions_account
    ON transactions(account_id);

CREATE INDEX IF NOT EXISTS idx_transactions_category
    ON transactions(category_id);

CREATE INDEX IF NOT EXISTS idx_transactions_date
    ON transactions(date);

CREATE INDEX IF NOT EXISTS idx_accounts_user
    ON accounts(user_id);

CREATE INDEX IF NOT EXISTS idx_categories_user
    ON categories(user_id);

CREATE INDEX IF NOT EXISTS idx_budgets_user
    ON budgets(user_id);



