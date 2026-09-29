# ♻️ Auronyx Backend

Auronyx is a digital waste-management and recycling platform that connects citizens, waste collectors, recyclers, and administrators through a unified backend.

The backend covers authentication, users, collectors, recyclers, waste identification, AI-assisted analysis, pricing, pickup management, physical verification, transactions, payments, wallet, recycling traceability, QR traceability, Kabadi Points, rewards, redemption, referrals, gamification, fraud detection, notifications, administration, and analytics.

> **Source of truth:** The current backend source code, configuration, migrations, controllers, services, entities, repositories, DTOs, and tests are the final authority. This README documents the target backend architecture and API contract described for Auronyx; exact implementation details should be kept synchronized with the repository.

---

## Table of Contents

- [Overview](#overview)
- [Objectives](#objectives)
- [Architecture](#architecture)
- [Technology Stack](#technology-stack)
- [Roles](#roles)
- [Authentication and Security](#authentication-and-security)
- [Module 1: Core and User](#module-1-core-and-user)
- [Module 2: Waste Operations](#module-2-waste-operations)
- [AI Integration](#ai-integration)
- [Pricing](#pricing)
- [Pickup Management](#pickup-management)
- [Physical Verification](#physical-verification)
- [Transactions](#transactions)
- [Payments](#payments)
- [Wallet](#wallet)
- [Recycling and Traceability](#recycling-and-traceability)
- [QR Traceability](#qr-traceability)
- [Module 3: Rewards and Ecosystem](#module-3-rewards-and-ecosystem)
- [Kabadi Points](#kabadi-points)
- [Rewards and Redemption](#rewards-and-redemption)
- [Referral](#referral)
- [Gamification](#gamification)
- [Fraud Detection](#fraud-detection)
- [Admin](#admin)
- [Analytics](#analytics)
- [Notifications](#notifications)
- [Database](#database)
- [Flyway](#flyway)
- [Project Structure](#project-structure)
- [Configuration](#configuration)
- [Installation](#installation)
- [Running](#running)
- [Testing](#testing)
- [API Reference](#api-reference)
- [Complete Business Flow](#complete-business-flow)
- [Data Integrity](#data-integrity)
- [Development Workflow](#development-workflow)
- [Troubleshooting](#troubleshooting)
- [Future Scope](#future-scope)
- [Verification Checklist](#verification-checklist)
- [License](#license)

---

## Overview

Traditional waste collection usually follows:

```text
Citizen
  ↓
Contacts collector
  ↓
Waste is weighed
  ↓
Price is decided
  ↓
Payment
  ↓
Waste leaves the user's visibility
```

Auronyx digitizes the complete lifecycle:

```text
Waste Booking
    ↓
AI-Assisted Identification
    ↓
Estimated Price
    ↓
Pickup Request
    ↓
Collector Acceptance
    ↓
Physical Verification
    ↓
Actual Weight
    ↓
Backend Final Price
    ↓
Transaction
    ↓
Payment
    ↓
Wallet / Kabadi Points
    ↓
Recycling
    ↓
QR Traceability
    ↓
Impact / Rewards
```

---

## Objectives

The backend is designed to:

- Digitize waste collection.
- Connect users with collectors.
- Provide AI-assisted waste identification.
- Provide configurable waste pricing.
- Separate AI estimates from physical verification.
- Calculate final prices on the backend.
- Process payments and wallet updates.
- Reward verified waste activity.
- Track recycling from collection to processing.
- Provide QR-based traceability.
- Give administrators operational control.
- Detect suspicious activity.
- Provide analytics and reports.

---

# Architecture

Auronyx backend is divided into three major modules:

```text
BACKEND
│
├── 1. CORE & USER MODULE
│
├── 2. WASTE OPERATIONS MODULE
│
└── 3. REWARDS & ECOSYSTEM MODULE
```

### 1. Core & User

Authentication, authorization, users, collectors, recyclers, notifications, location, and system services.

### 2. Waste Operations

Waste, pricing, AI, pickups, physical verification, transactions, payments, wallet, recycling, and QR traceability.

### 3. Rewards & Ecosystem

Kabadi Points, rewards, redemption, referral, gamification, fraud detection, admin, analytics, and reports.

---

# Technology Stack

| Technology | Purpose |
|---|---|
| Java | Backend language |
| Spring Boot | REST backend |
| Spring Security | Authentication and authorization |
| Spring Data JPA | Persistence |
| Hibernate | ORM |
| Maven | Build/dependency management |
| MySQL | Database |
| Flyway | Database migrations |
| JWT | Authentication |
| BCrypt | Password hashing |
| Bean Validation | Request validation |
| REST | API communication |
| JUnit | Testing |
| Mockito | Unit-test mocking |
| Python | AI service |
| FastAPI | AI service API |

Actual versions must be taken from the project's `pom.xml` and AI service configuration.

---

# Roles

## USER

Citizens who:

- Register/login.
- Manage their profile and location.
- Identify waste.
- Create pickups.
- Track pickups.
- Receive verified transaction value.
- Earn points.
- Redeem rewards.
- Track recycling.
- View impact.

## COLLECTOR

Collectors who:

- Manage profile and availability.
- View nearby requests.
- Accept/reject pickups.
- Update pickup status.
- Physically inspect waste.
- Record actual category and weight.
- Complete collection transactions.

## RECYCLER

Recycling partners who:

- Manage recycler profile.
- View incoming waste.
- Receive waste.
- Update recycling status.
- Process waste.
- Complete recycling records.

## ADMIN

Administrators who manage:

- Users
- Collectors
- Recyclers
- Waste
- Pricing
- Rewards
- Fraud alerts
- Analytics
- Reports

---

# Authentication and Security

Expected security flow:

```text
Request
  ↓
CORS / Security
  ↓
JWT Filter
  ↓
JWT Validation
  ↓
Security Context
  ↓
Role / Ownership Authorization
  ↓
Controller
  ↓
Service
  ↓
Repository
```

Security capabilities include, where implemented:

- Registration
- Login
- Logout
- JWT
- JWT filter
- BCrypt password hashing
- OTP
- OTP verification
- OTP expiration
- OTP attempt limits
- Forgot password
- Reset password
- Role-based authorization
- Ownership checks
- Admin authorization
- CORS
- Bean validation
- Centralized exception handling
- HTTP 401/403 handling

Never commit passwords, JWT secrets, API keys, OTP secrets, or production credentials.

---

# Module 1: Core and User

## Authentication APIs

```http
POST /api/auth/register
POST /api/auth/login
POST /api/auth/logout
POST /api/auth/send-otp
POST /api/auth/verify-otp
POST /api/auth/forgot-password
POST /api/auth/reset-password
```

## User APIs

```http
GET /api/users/me
PUT /api/users/me
PUT /api/users/me/location
PUT /api/users/me/language
GET /api/users/me/dashboard
GET /api/users/me/impact
```

## Collector APIs

```http
GET /api/collectors
GET /api/collectors/nearby
GET /api/collectors/{id}

GET /api/collector/profile
PUT /api/collector/profile
PUT /api/collector/availability
```

## Recycler APIs

```http
GET /api/recycler/profile
PUT /api/recycler/profile
```

## Notification APIs

```http
GET /api/notifications
GET /api/notifications/unread
PUT /api/notifications/{id}/read
PUT /api/notifications/read-all
DELETE /api/notifications/{id}
```

## System APIs

```http
GET /api/health
GET /api/config
GET /api/constants
```

---

# Module 2: Waste Operations

The main waste flow is:

```text
Waste
 ↓
AI Identification
 ↓
Price Estimate
 ↓
Pickup Booking
 ↓
Collector Assignment
 ↓
Collector Acceptance
 ↓
On The Way
 ↓
Physical Verification
 ↓
Actual Weight
 ↓
Backend Final Pricing
 ↓
Transaction
 ↓
Payment
 ↓
Wallet
 ↓
Recycling
 ↓
QR Traceability
```

---

## Waste Management

Supported conceptual categories:

```text
E-Waste
Plastic
Paper
Metal
Glass
Cardboard
Other
```

APIs:

```http
GET /api/waste/categories
GET /api/waste/categories/{id}
GET /api/waste/items
GET /api/waste/items/{id}
GET /api/waste/prices
GET /api/waste/prices/{categoryId}
```

Pricing and waste data should use one backend source of truth.

---

# AI Integration

Backend AI APIs:

```http
POST /api/ai/analyze
GET /api/ai/analysis/{id}
GET /api/ai/health
```

Internal AI service:

```http
POST /ai/predict
GET /ai/health
```

Typical prediction fields:

```json
{
  "category": "E-Waste",
  "item": "Mobile Phone",
  "condition": "USED",
  "estimatedWeight": 0.35,
  "priceMin": 80,
  "priceMax": 120,
  "confidence": 0.94
}
```

AI is an estimate, not the final financial authority.

```text
AI Estimate
    ↓
Collector Verification
    ↓
Actual Category
    ↓
Actual Weight
    ↓
Backend Pricing
    ↓
Final Transaction
```

If a mock AI provider is used, it must be clearly treated as a development fallback rather than a production ML model.

---

# Pricing

Preferred final pricing rule:

```text
Final Price = Actual Verified Weight × Configured Rate Per Kg
```

Example:

```text
Actual Weight = 0.42 kg
Rate = ₹250/kg

Final Price = 0.42 × 250
            = ₹105
```

Rules:

- Final price is calculated by the backend.
- Frontend final-price values must not be trusted.
- Actual verified weight is used.
- Category rate comes from backend configuration/data.
- Monetary calculations should use precise decimal arithmetic such as `BigDecimal`.
- Admin pricing changes must update the same source of truth used by transaction calculation.

---

# Pickup Management

APIs:

```http
POST /api/pickups
GET /api/pickups
GET /api/pickups/{id}
PUT /api/pickups/{id}
DELETE /api/pickups/{id}
GET /api/pickups/{id}/status
PUT /api/pickups/{id}/cancel
```

Collector operations:

```http
GET /api/collector/pickups
GET /api/collector/pickups/nearby
POST /api/collector/pickups/{id}/accept
POST /api/collector/pickups/{id}/reject
PUT /api/collector/pickups/{id}/status
```

Conceptual lifecycle:

```text
REQUESTED
    ↓
ACCEPTED
    ↓
ON_THE_WAY
    ↓
COLLECTED
    ↓
COMPLETED
```

Cancellation is handled according to the backend's business rules.

---

# Physical Verification

APIs:

```http
GET /api/collector/pickups/{id}/verification
POST /api/collector/pickups/{id}/verification
PUT /api/collector/pickups/{id}/verification
```

Verification can contain:

- Actual category
- Actual weight
- Condition
- Collector remarks
- Verification timestamp

Physical verification determines the data used for the final transaction calculation.

---

# Transactions

APIs:

```http
GET /api/transactions
GET /api/transactions/{id}
POST /api/transactions/{id}/complete
GET /api/transactions/{id}/receipt
```

A transaction can relate to:

```text
User
Collector
Pickup
Waste
Verification
Payment
Wallet
Recycling
Traceability
```

Transaction completion must respect required verification/payment conditions.

Financial operations should be idempotent.

---

# Payments

APIs:

```http
POST /api/payments
GET /api/payments/{id}
GET /api/payments/{id}/status
```

Payment flow:

```text
Transaction
    ↓
Payment Request
    ↓
Payment Processing
    ↓
Payment Confirmation
    ↓
Financial Update
```

A mock payment gateway may be used for development. Do not document it as a real production payment provider.

---

# Wallet

APIs:

```http
GET /api/wallet
GET /api/wallet/balance
GET /api/wallet/transactions
GET /api/wallet/transactions/{id}
```

Wallet represents monetary value.

```text
Wallet = Money
Kabadi Points = Reward Points
```

Cash rewards should credit the existing wallet rather than creating another wallet.

Duplicate wallet credits must be prevented.

---

# Recycling and Traceability

User APIs:

```http
GET /api/recycling
GET /api/recycling/{id}
GET /api/recycling/{id}/timeline
```

Recycler APIs:

```http
GET /api/recycler/incoming
GET /api/recycler/records
POST /api/recycler/records/{id}/receive
PUT /api/recycler/records/{id}/status
POST /api/recycler/records/{id}/processing
POST /api/recycler/records/{id}/complete
```

Conceptual lifecycle:

```text
COLLECTED
    ↓
SORTED / AGGREGATED
    ↓
TRANSPORT
    ↓
RECEIVED
    ↓
PROCESSING
    ↓
RECYCLED / RECOVERED
```

Use the actual status enum names from the implementation when maintaining this documentation.

---

# QR Traceability

APIs:

```http
POST /api/qr/generate/{transactionId}
GET /api/qr/transaction/{transactionId}
GET /api/qr/{code}
```

QR data may include:

- Transaction ID
- Waste category
- Weight
- Collection status
- Recycling status
- Safe collector/recycler information where appropriate

Public QR endpoints must never expose:

- Passwords
- JWTs
- Payment secrets
- API keys
- Sensitive personal information

---

# Module 3: Rewards and Ecosystem

Module 3 contains:

```text
Kabadi Points
Rewards
Redemption
Referral
Gamification
Fraud Detection
Admin
Analytics
```

---

# Kabadi Points

Reference conversion:

```text
50 KP   = ₹5
100 KP  = ₹10
500 KP  = ₹50
1000 KP = ₹100
```

The actual point-earning rule should be configurable.

Points should be earned only after the required verified/completed transaction.

No points should be awarded merely for:

- Image upload
- AI prediction
- Incomplete pickup
- Cancelled pickup

Point ledger statuses:

```text
PENDING
CREDITED
REDEEMED
REVERSED
```

APIs:

```http
GET /api/points
GET /api/points/balance
GET /api/points/ledger
GET /api/points/ledger/{id}
```

Point changes should be auditable and ledger-based.

---

# Rewards and Redemption

Reward types:

```text
CASH
COUPON
IMPACT_REWARD
```

Reward APIs:

```http
GET /api/rewards
GET /api/rewards/{id}
POST /api/rewards/{id}/redeem
```

Redemption APIs:

```http
GET /api/redemptions
GET /api/redemptions/{id}
```

Redemption must validate:

- Reward exists.
- Reward is active.
- Reward is available.
- User has sufficient points.
- User satisfies reward rules.

Cash rewards should use the existing wallet.

---

# Referral

APIs:

```http
GET /api/referrals
POST /api/referrals/apply
GET /api/referrals/history
GET /api/referrals/rewards
```

Referral rules should prevent:

- Self-referrals
- Duplicate referrals
- Invalid codes
- Repeated rewards

Referral rewards should be triggered only after the referred user completes the required verified activity.

---

# Gamification

Badges:

```text
First Recycler
10 Pickups
Eco Champion
E-Waste Warrior
```

Challenges can include:

```text
Complete 5 pickups
Recycle 10 kg of plastic
Recycle e-waste
Complete a monthly target
```

APIs:

```http
GET /api/badges
GET /api/badges/my
GET /api/challenges
GET /api/challenges/{id}
GET /api/challenges/my
```

Progress should come from real stored activity.

---

# Fraud Detection

Potential rules:

- Duplicate transactions
- Duplicate images
- Abnormally high weight
- Suspicious accounts
- Unusual point activity
- Suspicious pickup patterns

APIs:

```http
POST /api/fraud/check
GET /api/fraud/status/{transactionId}
```

Conceptual flow:

```text
Suspicious Activity
       ↓
Fraud Check
       ↓
Fraud Alert
       ↓
Admin Review
       ↓
Resolution
```

Fraud detection should not automatically punish legitimate users without an appropriate review process.

---

# Admin

## Users

```http
GET /api/admin/users
GET /api/admin/users/{id}
PUT /api/admin/users/{id}/status
```

## Collectors

```http
GET /api/admin/collectors
GET /api/admin/collectors/{id}
PUT /api/admin/collectors/{id}/verify
PUT /api/admin/collectors/{id}/status
```

## Recyclers

```http
GET /api/admin/recyclers
GET /api/admin/recyclers/{id}
PUT /api/admin/recyclers/{id}/verify
PUT /api/admin/recyclers/{id}/status
```

## Waste

```http
POST /api/admin/waste/categories
PUT /api/admin/waste/categories/{id}
DELETE /api/admin/waste/categories/{id}

POST /api/admin/waste/items
PUT /api/admin/waste/items/{id}
DELETE /api/admin/waste/items/{id}
```

## Pricing

```http
GET /api/admin/pricing
POST /api/admin/pricing
PUT /api/admin/pricing/{id}
```

## Rewards

```http
POST /api/admin/rewards
PUT /api/admin/rewards/{id}
DELETE /api/admin/rewards/{id}
PUT /api/admin/rewards/{id}/status
```

## Fraud Alerts

```http
GET /api/admin/fraud-alerts
GET /api/admin/fraud-alerts/{id}
PUT /api/admin/fraud-alerts/{id}/review
PUT /api/admin/fraud-alerts/{id}/resolve
```

Admin APIs must be ADMIN-only.

---

# Analytics

APIs:

```http
GET /api/admin/dashboard
GET /api/admin/analytics
GET /api/admin/analytics/users
GET /api/admin/analytics/pickups
GET /api/admin/analytics/transactions
GET /api/admin/analytics/recycling
GET /api/admin/analytics/revenue
GET /api/admin/reports
```

Analytics should use actual persisted platform data.

Possible metrics:

- Users
- Pickups
- Transactions
- Revenue
- Recycling
- Waste quantities
- Platform activity

Do not fabricate production statistics.

---

# Notifications

Notifications can be generated for:

- Pickup requested
- Pickup accepted
- Collector on the way
- Pickup completed
- Payment completed
- Points credited
- Reward redeemed
- Recycling updates
- Suspicious activity

APIs:

```http
GET /api/notifications
GET /api/notifications/unread
PUT /api/notifications/{id}/read
PUT /api/notifications/read-all
DELETE /api/notifications/{id}
```

A reusable notification service should be shared across modules.

---

# Database

Important conceptual entities:

```text
User
Role
Collector
Recycler

WasteCategory
WasteItem
AIPrediction

Pickup
PickupStatusHistory
WasteVerification

Transaction
Payment

Wallet
WalletTransaction

PointLedger
Reward
Redemption

Referral

Badge
UserBadge
Challenge

RecyclingRecord
TraceabilityRecord

Notification
FraudAlert
```

Only entities actually present in the source code should be listed as implemented.

## Relationship Overview

```text
User
├── Pickup
├── Transaction
├── Wallet
├── PointLedger
├── Redemption
├── Referral
└── Notification

Collector
├── Pickup
└── WasteVerification

Recycler
└── RecyclingRecord

Pickup
├── User
├── Collector
├── WasteVerification
└── Transaction

Transaction
├── Payment
├── Wallet
├── RecyclingRecord
└── TraceabilityRecord

Reward
└── Redemption

Badge
└── UserBadge

Challenge
└── User Progress

FraudAlert
└── Transaction / User
```

Exact relationships and foreign keys must follow the entity mappings and migrations.

---

# Flyway

Flyway manages database schema changes.

Typical location:

```text
src/main/resources/db/migration/
```

Rules:

1. Never rewrite an applied migration.
2. Never delete an applied migration.
3. Never reuse a migration version.
4. Continue the existing migration sequence.
5. Test migrations before deployment.
6. Keep schema changes explicit.

Migration version numbers must be read from the actual repository.

---

# Project Structure

Typical high-level structure:

```text
Auronyx/
├── backend/
│   ├── README.md
│   └── kabadiwala-backend/
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/
│       │   │   └── resources/
│       │   └── test/
│       ├── pom.xml
│       └── ...
└── frontend/
    └── ...
```

The exact package structure should be documented from the repository rather than invented.

---

# Configuration

Common configuration areas:

```text
Database
JWT
OTP
CORS
AI service
Payment
Application port
Flyway
Logging
```

Typical placeholder configuration:

```properties
DATABASE_URL=YOUR_DATABASE_URL
DATABASE_USERNAME=YOUR_DATABASE_USERNAME
DATABASE_PASSWORD=YOUR_DATABASE_PASSWORD

JWT_SECRET=YOUR_JWT_SECRET
JWT_EXPIRATION=YOUR_JWT_EXPIRATION

AI_SERVICE_URL=YOUR_AI_SERVICE_URL

CORS_ALLOWED_ORIGINS=YOUR_FRONTEND_ORIGIN
```

Use the project's actual configuration names.

Never place real secrets in README.

---

# Installation

## Prerequisites

- Java version required by `pom.xml`
- Maven or Maven Wrapper
- MySQL
- Git
- Python/FastAPI if the AI service is separate

## Clone

```bash
git clone <repository-url>
cd Auronyx
```

## Backend

```bash
cd backend
cd kabadiwala-backend
```

If the actual repository has a different structure, follow that structure.

## Database

Example:

```sql
CREATE DATABASE auronyx;
```

Configure the database credentials using environment variables or application configuration.

## Start

```bash
mvn spring-boot:run
```

Windows Maven Wrapper:

```powershell
.\mvnw spring-boot:run
```

---

# Running

## Compile

```bash
mvn clean compile
```

## Test

```bash
mvn test
```

## Package

```bash
mvn package
```

## Full build

```bash
mvn clean package
```

If Maven Wrapper is available:

```powershell
.\mvnw clean package
```

## JAR

After packaging:

```bash
java -jar target/<application>.jar
```

Use the actual generated JAR filename.

---

# Testing

Recommended test coverage:

## Authentication

```text
Registration
Login
JWT validation
OTP
Password reset
Role authorization
```

## User

```text
Profile
Location
Language
Ownership
```

## Pickup

```text
Create
Accept
Reject
Status transitions
Cancellation
```

## Waste

```text
Categories
Items
Pricing
AI response
Physical verification
```

## Transactions

```text
Price calculation
Completion
Payment
Wallet
Idempotency
```

## Rewards

```text
Points
Point ledger
Redemption
Referral
Badges
Challenges
Fraud
```

## Admin

```text
User management
Collector management
Recycler management
Pricing
Rewards
Fraud
Analytics
```

---

# API Reference

## API Groups

| Group | Base Path |
|---|---|
| Authentication | `/api/auth` |
| Users | `/api/users` |
| Collectors | `/api/collectors` |
| Collector Operations | `/api/collector` |
| Recycler | `/api/recycler` |
| Notifications | `/api/notifications` |
| System | `/api/health` |
| Waste | `/api/waste` |
| AI | `/api/ai` |
| Pickup | `/api/pickups` |
| Transactions | `/api/transactions` |
| Payments | `/api/payments` |
| Wallet | `/api/wallet` |
| Recycling | `/api/recycling` |
| QR | `/api/qr` |
| Points | `/api/points` |
| Rewards | `/api/rewards` |
| Redemption | `/api/redemptions` |
| Referral | `/api/referrals` |
| Badges | `/api/badges` |
| Challenges | `/api/challenges` |
| Fraud | `/api/fraud` |
| Admin | `/api/admin` |

All endpoint contracts should be verified against the current controllers.

---

# Complete Business Flow

```text
USER
  ↓
REGISTER / LOGIN
  ↓
JWT
  ↓
WASTE IDENTIFICATION
  ↓
AI ESTIMATE
  ↓
PRICE ESTIMATE
  ↓
PICKUP BOOKING
  ↓
COLLECTOR ASSIGNMENT
  ↓
ACCEPTANCE
  ↓
ON THE WAY
  ↓
PHYSICAL VERIFICATION
  ↓
ACTUAL WEIGHT
  ↓
FINAL BACKEND PRICE
  ↓
TRANSACTION
  ↓
PAYMENT
  ↓
WALLET
  ↓
KABADI POINTS
  ↓
REWARDS / REDEMPTION
  ↓
RECYCLING
  ↓
QR TRACEABILITY
  ↓
IMPACT / ANALYTICS
```

---

# Data Integrity

Important rules:

### Backend is the financial source of truth

The frontend must not determine the final transaction price.

### AI is assistive

```text
AI = Estimate
Collector = Physical Verification
Backend = Final Calculation
```

### Wallet and Points are separate

```text
Wallet → Money
Points → Rewards
```

### Points require verified activity

Points should not be credited for image upload or incomplete transactions.

### Ledger-based points

Every point change should have an auditable ledger entry.

### Idempotency

Protect:

```text
Payment
Wallet Credit
Point Credit
Redemption
Transaction Completion
Referral Reward
```

from duplicate processing.

### Fraud

Fraud alerts should support investigation rather than blindly punishing users.

---

# Error Handling

Expected HTTP statuses:

```text
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
409 Conflict
500 Internal Server Error
```

Typical errors:

- Validation errors
- Authentication errors
- Authorization errors
- Resource not found
- Business rule violations
- Duplicate/conflict errors
- Internal errors

The exact error JSON must follow the implementation's global exception handler.

---

# Development Workflow

```text
Requirement
    ↓
Inspect Existing Architecture
    ↓
Entity / Domain Change
    ↓
Flyway Migration
    ↓
Repository
    ↓
DTO
    ↓
Service
    ↓
Controller
    ↓
Validation
    ↓
Security
    ↓
Tests
    ↓
Compile
    ↓
Package
    ↓
API Verification
```

Guidelines:

1. Reuse existing architecture.
2. Avoid duplicate entities.
3. Avoid duplicate wallets.
4. Avoid duplicate pricing sources.
5. Keep controllers thin.
6. Keep business logic in services.
7. Use DTOs for API contracts.
8. Validate input.
9. Use transactions for financial operations.
10. Add migrations for schema changes.
11. Add tests for important rules.
12. Keep secrets outside source control.
13. Preserve existing API contracts.

---

# Troubleshooting

## MySQL Connection Failure

Check:

- MySQL is running.
- Database exists.
- Credentials are correct.
- JDBC URL is correct.
- Database port is accessible.

## Flyway Failure

Check:

- Migration version.
- Filename.
- SQL syntax.
- Existing schema history.
- Duplicate versions.

Never rewrite an applied migration.

## Port Already in Use

Find the process using the configured port or change the development port.

## AI Service Unavailable

Check:

- AI process is running.
- `AI_SERVICE_URL` is correct.
- Endpoint is correct.
- Request/response contract matches.

## CORS Error

Check:

- Frontend origin.
- Backend CORS configuration.
- Allowed methods.
- Authorization headers.
- Host/port.

## JWT Failure

Check:

- JWT secret.
- Token expiration.
- `Authorization: Bearer <token>`.
- Security filter.
- User status.
- Role.

## Maven Failure

Run:

```bash
mvn clean test
```

or:

```powershell
.\mvnw clean test
```

Fix the first meaningful compilation/test error.

---

# Future Scope

Potential future enhancements:

- Production ML waste classification
- Real payment gateway
- Advanced fraud ML
- Live GPS collector tracking
- SMS/WhatsApp notifications
- Voice booking
- Advanced EPR integrations
- IoT weighing
- Offline collector mode
- More regional languages
- Advanced environmental calculations
- Automated recycler integrations
- Advanced analytics

Only features not already implemented should be listed as future scope.

---

# Limitations

Development-stage limitations may include:

- Mock AI when a production model is unavailable.
- Mock payment when no real gateway is configured.
- Development OTP provider.
- Limited external notification providers.
- Local development infrastructure.
- Lack of official EPR integration unless separately implemented.

These must be reconciled with the actual repository before presenting them as current limitations.

---

# Verification Checklist

## Core & User

- [ ] Registration
- [ ] Login
- [ ] Logout
- [ ] JWT
- [ ] OTP
- [ ] Password reset
- [ ] User profile
- [ ] Location
- [ ] Language
- [ ] Collector profile
- [ ] Recycler profile
- [ ] Notifications
- [ ] RBAC

## Waste Operations

- [ ] Waste categories
- [ ] Waste items
- [ ] Pricing
- [ ] AI
- [ ] Pickup
- [ ] Collector acceptance
- [ ] Pickup status
- [ ] Physical verification
- [ ] Final price
- [ ] Transaction
- [ ] Payment
- [ ] Wallet
- [ ] Recycling
- [ ] QR

## Rewards

- [ ] Points
- [ ] Point ledger
- [ ] Rewards
- [ ] Redemption
- [ ] Referral
- [ ] Badges
- [ ] Challenges
- [ ] Fraud
- [ ] Admin
- [ ] Analytics

## Infrastructure

- [ ] MySQL
- [ ] Flyway
- [ ] Compile
- [ ] Tests
- [ ] Package
- [ ] Startup
- [ ] Health endpoint
- [ ] CORS
- [ ] Secrets protected

---

# Production Readiness Checklist

```text
[ ] Production database
[ ] Secure secrets
[ ] Strong JWT secret
[ ] HTTPS
[ ] Restricted CORS
[ ] Real payment provider
[ ] Production AI
[ ] OTP provider
[ ] Notification provider
[ ] Database backups
[ ] Flyway verification
[ ] Monitoring
[ ] Error tracking
[ ] Rate limiting
[ ] Security review
[ ] QR privacy review
[ ] Wallet idempotency
[ ] Payment idempotency
[ ] Points ledger integrity
[ ] Fraud rules review
```

---

# Design Principles

### Backend is the source of truth

Financial and business-critical values must be validated server-side.

### AI is assistive

AI provides estimates; physical verification determines verified transaction information.

### Verification before finalization

Final financial values should be based on verified category and weight.

### Wallet and points are separate

Money and reward points are different domains.

### Traceability

Waste should remain traceable through its recycling lifecycle.

### Role-based access

Users should only perform actions allowed for their role.

### Auditability

Points, payments, wallet transactions, and redemptions should be traceable.

### Maintainability

Reuse existing services and avoid unnecessary architecture.

---

# Module Integration

```text
┌────────────────────────────┐
│  CORE & USER               │
│                            │
│ Auth                       │
│ Users                      │
│ Collectors                 │
│ Recyclers                  │
│ Notifications              │
└─────────────┬──────────────┘
              │
              ▼
┌────────────────────────────┐
│  WASTE OPERATIONS          │
│                            │
│ Waste                      │
│ AI                         │
│ Pricing                    │
│ Pickup                     │
│ Verification               │
│ Transaction                │
│ Payment                    │
│ Wallet                     │
│ Recycling                  │
│ QR                         │
└─────────────┬──────────────┘
              │
              ▼
┌────────────────────────────┐
│  REWARDS & ECOSYSTEM       │
│                            │
│ Points                     │
│ Rewards                    │
│ Redemption                 │
│ Referral                   │
│ Gamification               │
│ Fraud                      │
│ Admin                      │
│ Analytics                  │
└────────────────────────────┘
```

---

# Environmental Impact

The backend can support impact metrics derived from actual waste/recycling records:

- Waste collected
- Waste recycled
- E-waste
- Plastic
- Paper
- Completed pickups
- Recycling transactions

Environmental calculations should use documented, deterministic formulas. Arbitrary environmental claims should not be presented as verified scientific measurements.

---

# EPR Support

The platform can maintain digital traceability records useful for EPR-related workflows:

```text
Waste Category
Quantity
Weight
Collection
Collector
Recycler
Handover
Processing
Recycling Status
```

Auronyx should not be described as a replacement for official government EPR systems unless an authorized integration exists.

---

# Localization

The backend can store user language preferences.

Example:

```text
English
Hindi
Regional Languages
```

API:

```http
PUT /api/users/me/language
```

---

# Frontend Integration

Typical architecture:

```text
React Frontend
      ↓
Axios / HTTP
      ↓
Spring Boot REST API
      ↓
JWT / Security
      ↓
Controller
      ↓
Service
      ↓
Repository
      ↓
MySQL
```

The frontend must not be trusted for:

- Final price
- Wallet balance
- Point balance
- Admin authorization
- Payment confirmation
- Transaction completion

---

# Backend Layering

```text
Controller
    ↓
DTO / Validation
    ↓
Service
    ↓
Repository
    ↓
Entity
    ↓
Database
```

Security surrounds the API:

```text
Request
  ↓
Security
  ↓
JWT
  ↓
Authorization
  ↓
Controller
  ↓
Service
```

---

# Avoid Unnecessary Complexity

Do not introduce unnecessary:

- Microservices
- Message brokers
- Event streaming
- Duplicate databases
- Duplicate wallets
- Duplicate pricing systems
- Duplicate user systems
- Unnecessary frameworks

unless a genuine requirement exists.

---

# Repository Hygiene

Do not commit:

```text
.env
Production passwords
JWT secrets
API keys
Private certificates
Sensitive database dumps
IDE metadata
Build output
```

Use `.gitignore` for generated files and local secrets.

---

# License

Add the project's applicable license here before public production distribution.

---

# Maintained As Part Of

```text
Auronyx
Digital Waste Management & Recycling Platform
```

Technology direction:

```text
Backend: Spring Boot
Database: MySQL
Migration: Flyway
Authentication: JWT + OTP
AI: Python / FastAPI
```

---

# Auronyx Vision

Auronyx aims to transform traditional waste collection into a transparent, technology-driven circular-economy platform.

```text
Citizens
    ↓
Waste Collectors
    ↓
Verified Transactions
    ↓
Recyclers
    ↓
Recycling
    ↓
Traceability
    ↓
Rewards
    ↓
Environmental Impact
```

The long-term goal is a waste ecosystem that is:

```text
Digital
Transparent
Verified
Rewarding
Traceable
Sustainable
```

---

## Documentation Maintenance

When the backend changes, update this README together with:

- API contracts
- Entity changes
- Database migrations
- Authentication rules
- Configuration
- Business rules
- Testing commands
- Module boundaries

The source code remains the final technical source of truth.
