# Personal Financier

Backend application for managing personal finances.

The system allows users to manage financial accounts, track transactions, categorize expenses, plan budgets, create
financial goals, and analyze spending patterns.

This project focuses on building a secure REST API using the Spring ecosystem, modern backend development practices, and
production-oriented security mechanisms.

---

# Tech Stack

## Backend

* Java 21
* Spring Boot
* Spring Security
* Spring Data JPA
* MapStruct

## Database

* PostgreSQL
* Flyway (database migrations)

## Caching & Infrastructure

* Redis

## Security

* JWT Authentication
* Refresh Token Rotation
* Refresh Token Hashing (SHA-256)
* JWT Blacklist Logout (Redis)
* Email Verification
* Password Reset
* Login Attempt Protection
* Rate Limiting (Bucket4j + Redis)
* Security Headers Configuration
* OWASP HTML Sanitizer

## Tools

* Maven
* Docker
* Swagger / OpenAPI
* Git
* SpotBugs
* FindSecBugs
* OWASP Dependency Check

---

# Features

## Authentication & Security

* User registration with email verification
* JWT authentication
* Refresh token rotation
* Refresh token hashing
* JWT token revocation
* Secure logout
* Password reset workflow
* Login attempt protection
* Rate limiting using Redis and Bucket4j
* Security headers configuration
* Input sanitization

## Financial Management

* Account management
* Transaction tracking
* Transaction categorization
* Budget management
* Financial goals management
* Money transfers between accounts
* Financial analytics

## API Design

* DTO-based architecture
* MapStruct entity mapping
* Global exception handling
* Pagination support
* Validation using Jakarta Validation

---

# Security Architecture

The project implements multiple layers of API protection.

Security mechanisms include:

* Stateless JWT access tokens
* Refresh token rotation
* Refresh token hashing (SHA-256)
* Redis-based token revocation
* Login attempt protection against brute-force attacks
* Redis-backed rate limiting
* Security HTTP headers
* Email verification during registration
* Password reset workflow
* Input sanitization

## Authentication Flow

Client request pipeline:

```text
Client
  │
  ▼
RateLimitFilter
  │
  ▼
JwtAuthFilter
  │
  ▼
Spring Security
  │
  ▼
Controllers
```

## Token Lifecycle

```text
Login
  │
  ▼
Access Token (short-lived JWT)
Refresh Token (hashed and stored in database)
  │
  ▼
Refresh Token Rotation
  │
  ▼
Token Revocation Support
```

Access tokens are stateless and short-lived, while refresh tokens are securely stored and can be revoked.

---

# Project Structure

The application follows a layered architecture.

```text
controller
service
repository
entity
dto
mapper
security
exception
config
```

Architecture flow:

```text
Controller → Service → Repository → Database
```

Additional layers:

* DTO layer for API communication
* MapStruct for entity ↔ DTO mapping
* Security layer with JWT authentication
* Global exception handling
* Validation layer
* Redis integration layer

---

# Main Entities

## User

Application user with authentication, authorization, and email verification.

## Account

Financial accounts such as:

* Cash
* Card
* Bank
* Crypto

Supports optimistic locking and safe deletion.

## Transaction

Records:

* Income
* Expense
* Transfer

Supports optimistic locking.

## Category

Transaction categories for income and expenses.

Supports safe deletion while preserving transaction history.

## Budget

Allows users to set spending limits and monitor expenses.

Supports pagination.

## Goal

Financial goal tracking with progress monitoring.

Supports pagination.

---

# Financial Analytics

The application provides analytics features such as:

* Monthly expenses analysis
* Top spending categories
* Budget monitoring
* Goal progress tracking

---

# API

The application exposes a REST API.

Example endpoints:

```http
POST /auth/register
POST /auth/login
POST /auth/refresh
POST /auth/logout

GET /accounts
POST /accounts

GET /transactions
POST /transactions

GET /budgets
POST /budgets

GET /goals
POST /goals
```

API documentation is available via Swagger UI.

---

# Database

The project uses:

* PostgreSQL
* Flyway migrations

Database schema changes are managed through versioned migration scripts.

---

# Redis Usage

Redis is used for:

* JWT Blacklist
* Rate Limiting
* Login Attempt Protection

Automatic expiration is handled using Redis TTL.

---

# Testing

Implemented testing stack:

* JUnit 5
* Mockito
* Spring Security Test

The project includes unit and security-focused tests for core business logic and authentication components.

---

# Running the Project

Clone the repository:

```bash
git clone https://github.com/Develop2Den/Personal-financier.git
```

Navigate to the project folder:

```bash
cd Personal-financier
```

Start PostgreSQL and Redis.

Run the application:

```bash
mvn spring-boot:run
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

---

# Learning Purpose

This project was built to practice modern backend development using the Spring ecosystem, focusing on secure REST API
design, scalable architecture, security best practices, and real-world backend engineering concepts.

---

# Author

Denis Denisov

GitHub:
https://github.com/Develop2Den


