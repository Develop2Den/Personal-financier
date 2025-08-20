create SCHEMA IF NOT EXISTS finance;

-- Таблица пользователей
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

-- Таблица категорий
CREATE TABLE IF NOT EXISTS categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    type VARCHAR(20) NOT NULL -- INCOME или EXPENSE
);

-- Таблица транзакций
CREATE TABLE IF NOT EXISTS transactions (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id INT NOT NULL REFERENCES categories(id),
    amount NUMERIC(10,2) NOT NULL,
    type VARCHAR(20) NOT NULL, -- INCOME или EXPENSE
    description VARCHAR(255),
    transaction_date TIMESTAMP NOT NULL DEFAULT now(),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

-- Таблица счетов
CREATE TABLE IF NOT EXISTS accounts (
                                        id SERIAL PRIMARY KEY,
                                        name VARCHAR(100) NOT NULL,
                                        balance NUMERIC(12,2) NOT NULL DEFAULT 0,
                                        user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                        created_at TIMESTAMP NOT NULL DEFAULT now(),
                                        updated_at TIMESTAMP NOT NULL DEFAULT now()
);

-- Таблица бюджетов
CREATE TABLE IF NOT EXISTS budgets (
                                       id SERIAL PRIMARY KEY,
                                       limit_amount NUMERIC(12,2) NOT NULL,
                                       start_date DATE NOT NULL,
                                       end_date DATE NOT NULL,
                                       period VARCHAR(20) NOT NULL, -- например, MONTHLY, WEEKLY
                                       category_id INT REFERENCES categories(id),
                                       owner_id INT REFERENCES users(id),
                                       created_at TIMESTAMP NOT NULL DEFAULT now(),
                                       updated_at TIMESTAMP NOT NULL DEFAULT now()
);

-- Таблица целей
CREATE TABLE IF NOT EXISTS goals (
                                     id SERIAL PRIMARY KEY,
                                     name VARCHAR(100) NOT NULL,
                                     target_amount NUMERIC(12,2) NOT NULL,
                                     current_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
                                     start_date DATE NOT NULL,
                                     end_date DATE NOT NULL,
                                     user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                     created_at TIMESTAMP NOT NULL DEFAULT now(),
                                     updated_at TIMESTAMP NOT NULL DEFAULT now()
);



