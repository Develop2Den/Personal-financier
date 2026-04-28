# Personal Financier

Backend application for managing personal finances.

The system allows users to manage financial accounts, track transactions, categorize expenses, and plan budgets and financial goals.

This project focuses on building a **secure REST API** using the Spring ecosystem and modern backend development practices.

---

# Tech Stack

## Backend

* Java
* Spring Boot
* Spring Security
* Spring Data JPA
* MapStruct

## Database

* PostgreSQL
* Flyway (database migrations)

## Security

* JWT authentication
* Refresh token rotation
* Refresh token hashing (SHA-256)
* JWT blacklist logout
* Email verification
* Login attempt protection
* Rate limiting (Bucket4j)
* Security headers configuration

## Tools

* Maven
* Docker
* Swagger / OpenAPI
* Git

---

# Features

* User registration with email verification
* JWT authentication
* Secure login flow
* Account management
* Transaction tracking
* Transaction categorization
* Budget management
* Financial goals
* DTO mapping using MapStruct
* Global exception handling
* Login attempt protection
* JWT blacklist logout
* Rate limiting using Bucket4j
* Security headers configuration

---

# Security Architecture

The project implements multiple layers of API protection.

Security mechanisms include:

* Stateless JWT access tokens
* Refresh token rotation
* Refresh token hashing (SHA-256)
* Token revocation support
* Login attempt protection against brute force attacks
* Rate limiting using Bucket4j
* Security HTTP headers
* Email verification during registration

### Authentication Flow

Client request pipeline:

```
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

### Token Lifecycle

```
Login
  │
  ▼
Access Token (short-lived JWT)
Refresh Token (stored in database)
  │
  ▼
Refresh token rotation on refresh
```

Access tokens are stateless and short-lived, while refresh tokens are stored in the database and can be revoked.

---

# Project Structure

The application follows a layered architecture.

```
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

```
Controller → Service → Repository → Database
```

Additional layers:

* DTO layer for API communication
* MapStruct for entity ↔ DTO mapping
* Security layer with JWT authentication
* Global exception handling

---

# Main Entities

### User

Application user with authentication and email verification.

### Account

Financial accounts such as:

* Cash
* Card
* Bank
* Crypto

### Transaction

Records income and expense operations.

### Category

Defines transaction categories.

### Budget

Allows users to set spending limits.

### Goal

Financial goals tracking.

---

# API

The application exposes a REST API.

Example endpoints:

```
POST /auth/register
POST /auth/login
POST /auth/logout

GET /accounts
POST /accounts

GET /transactions
POST /transactions
```

API documentation is available via **Swagger UI**.

---

# Running the Project

Clone the repository:

```
git clone https://github.com/Develop2Den/Personal-financier.git
```

Navigate to the project folder:

```
cd Personal-financier
```

Run the application:

```
mvn spring-boot:run
```

---

# Learning Purpose

This project was built to practice backend development with the Spring ecosystem, focusing on building **secure REST APIs** and applying common backend architecture patterns.

---

# Author

Denis Denisov

GitHub:
https://github.com/Develop2Den

