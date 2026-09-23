# Implementation Plan: Kabadiwala Backend Module 2 — Waste Operations Module [COMPLETED]

Implement the end-to-end operational engine for **Kabadiwala** within the existing Maven/Spring Boot backend project at `KABADIWALA/backend/kabadiwala-backend`.

Module 2 covers the complete waste lifecycle:
$$\text{Waste} \to \text{AI Identification} \to \text{Price Estimation} \to \text{Pickup Booking} \to \text{Collector Assignment} \to \text{Collector Acceptance} \to \text{On the Way} \to \text{Physical Verification} \to \text{Actual Weight} \to \text{Backend Pricing} \to \text{Transaction} \to \text{Payment} \to \text{Wallet} \to \text{Recycling Lifecycle} \to \text{QR Traceability}$$

> [!NOTE]
> **Status**: All features, services, controllers, entities, repositories, state machines, and automated tests are fully implemented, verified, and packaged into `kabadiwala-backend-0.0.1-SNAPSHOT.jar`.

---

## User Review Required

> [!IMPORTANT]
> **Existing Baseline State**: In the repository, files in `kabadiwala-backend` were scaffolded as 0-byte stubs. In order for Module 2 to compile and run with Maven, we will implement the clean Module 1 baseline classes (`User`, `Role`, `Collector`, `Recycler`, `Notification`, `SecurityConfig`, `JwtService`, `GlobalExceptionHandler`, etc.) along with `pom.xml` and `application.properties`, and then fully implement all Module 2 features and submodules. Module 3 files (`Reward`, `PointLedger`, `Badge`, `Challenge`, `EPRRecord`) will remain un-implemented in accordance with strict scoping rules.

> [!NOTE]
> **Pricing Source of Truth & Security**: The frontend will never be trusted for final price, weight, or wallet credit. All calculations use `BigDecimal` with half-up rounding, and client-supplied `finalAmount` or `balance` overrides will be rejected/ignored.

---

## Proposed Changes & Architecture

### 1. Build & Core Configuration

#### [MODIFY] [`pom.xml`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/pom.xml)
- Configure Spring Boot 3.3.4 parent, Java 21 compatibility.
- Dependencies: `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-security`, `spring-boot-starter-validation`, `mysql-connector-j`, `flyway-core`, `flyway-mysql`, `jjwt` (0.12.5), `zxing` (3.5.3 for QR code), `h2` (test scope for self-contained unit/integration test execution), `spring-boot-starter-test`.

#### [MODIFY] [`application.properties`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/resources/application.properties)
- Configurable environment properties:
  - `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`
  - `JWT_SECRET`, `JWT_EXPIRATION`
  - `AI_SERVICE_URL` (default `http://localhost:8000`)
  - `UPLOAD_MAX_SIZE` (default 10MB)
  - `UPLOAD_ALLOWED_TYPES` (`image/jpeg,image/png,image/webp`)

---

### 2. Module 1 Baseline (Reused by Module 2)

#### [MODIFY] [`User.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/entity/User.java) & [`Role.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/entity/Role.java)
- Standard user entity with roles (`USER`, `COLLECTOR`, `RECYCLER`, `ADMIN`).

#### [MODIFY] [`Collector.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/entity/Collector.java) & [`Recycler.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/entity/Recycler.java)
- Profiles linked to User with service area, coordinates (latitude, longitude), vehicle/business details, active status.

#### [MODIFY] Security & Common Packages
- [`SecurityConfig.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/config/SecurityConfig.java), [`JwtService.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/security/JwtService.java), [`JwtAuthenticationFilter.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/security/JwtAuthenticationFilter.java), [`SecurityUtils.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/security/SecurityUtils.java).
- Standard exception hierarchy: [`GlobalExceptionHandler.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/exception/GlobalExceptionHandler.java), [`ResourceNotFoundException.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/exception/ResourceNotFoundException.java), [`UnauthorizedException.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/exception/UnauthorizedException.java), [`InvalidTransactionException.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/exception/InvalidTransactionException.java).
- Common response DTO: `ApiResponse<T>`.

---

### 3. Module 2: Waste Management & Configurable Pricing

#### [MODIFY] [`WasteCategory.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/entity/WasteCategory.java) & [`Waste.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/entity/Waste.java)
- `WasteCategory`: id, name, description, active, unit, ratePerKg (`BigDecimal`), createdAt, updatedAt.
- `Waste` (item): id, category (ManyToOne), name, description, active, createdAt, updatedAt.
- Seeded categories: E-Waste (₹250/kg), Plastic (₹15/kg), Paper (₹12/kg), Metal (₹35/kg), Glass (₹5/kg), Cardboard (₹10/kg), Other (₹8/kg).

#### [MODIFY] [`WasteCategoryRepository.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/repository/WasteCategoryRepository.java) & [`WasteRepository.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/repository/WasteRepository.java)
- Active lookup queries, category name checks.

#### [MODIFY] [`WasteService.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/service/WasteService.java) & [`PricingService.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/service/PricingService.java)
- Configurable pricing engine: `calculatePrice(WasteCategory category, BigDecimal weight)` returning `weight.multiply(rate).setScale(2, RoundingMode.HALF_UP)`.
- Rejects negative or zero weights.

#### [MODIFY] [`WasteController.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/controller/WasteController.java)
- `GET /api/waste/categories`
- `GET /api/waste/categories/{id}`
- `GET /api/waste/items`
- `GET /api/waste/items/{id}`
- `GET /api/waste/prices`
- `GET /api/waste/prices/{categoryId}`

---

### 4. Module 2: AI / ML Integration

#### [MODIFY] [`AIPrediction.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/entity/AIPrediction.java)
- Stores estimation record: user, category, item, condition, estimatedWeight, priceMin, priceMax, confidence, createdAt.

#### [MODIFY] [`AIService.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/service/AIService.java)
- Validates file upload: non-null, size <= max size, MIME type in allowed list, safe extension.
- REST client calling FastAPI `POST ${AI_SERVICE_URL}/ai/predict` and `GET ${AI_SERVICE_URL}/ai/health`.
- Fault-tolerant fallback: `MockAIProvider` ensures that if FastAPI is unreachable, the system gracefully generates realistic estimations without crashing.
- Business rule: AI prediction is strictly for estimation and cannot directly complete transactions or update wallets.

#### [MODIFY] [`AIController.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/controller/AIController.java)
- `POST /api/ai/analyze` (multipart/form-data)
- `GET /api/ai/analysis/{id}`
- `GET /api/ai/health`

---

### 5. Module 2: Pickup Management & Collector Operations

#### [MODIFY] [`Pickup.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/entity/Pickup.java) & [`PickupStatusHistory.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/entity/PickupStatusHistory.java)
- Pickup: user, wasteCategory, wasteItem, estimatedQuantity, estimatedWeight, address, city, state, pincode, latitude, longitude, scheduledDate, scheduledTime, notes, assignedCollector, status (`REQUESTED`, `ACCEPTED`, `ON_THE_WAY`, `COLLECTED`, `COMPLETED`, `CANCELLED`).
- Status transition validation enforced in service layer:
  - `REQUESTED` $\to$ `ACCEPTED` or `CANCELLED`
  - `ACCEPTED` $\to$ `ON_THE_WAY` or `CANCELLED`
  - `ON_THE_WAY` $\to$ `COLLECTED`
  - `COLLECTED` $\to$ `COMPLETED`
  - Rejects backwards or unauthorized transitions.

#### [MODIFY] [`PickupService.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/service/PickupService.java) & [`CollectorService.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/service/CollectorService.java)
- User ownership check: User can only access, update, or cancel their own pickups (derived from JWT).
- Collector operations:
  - View eligible nearby requests (`GET /api/collector/pickups/nearby`).
  - View assigned pickups (`GET /api/collector/pickups`).
  - Accept request (`POST /api/collector/pickups/{id}/accept`).
  - Reject request (`POST /api/collector/pickups/{id}/reject`).
  - Update status (`PUT /api/collector/pickups/{id}/status`).
- Geospatial/proximity abstraction: calculates distances or filters by service area/city cleanly.

#### [MODIFY] [`PickupController.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/controller/PickupController.java) & [`CollectorController.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/controller/CollectorController.java)
- Full API implementations matching specifications.

---

### 6. Module 2: Waste Verification

#### [NEW] `WasteVerification.java` & `WasteVerificationRepository.java`
- Physical verification entity: pickup, collector, actualCategory, actualWasteItem, actualWeight, condition, notes, verificationTimestamp.
- Validates:
  - Weight must be positive and within reasonable limits.
  - Actual category must be active.
  - Collector must be the assigned collector.
  - Prevent duplicate or conflicting verifications.
  - Rejects verification once transaction is completed.

#### [MODIFY] [`CollectorController.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/controller/CollectorController.java)
- `GET /api/collector/pickups/{id}/verification`
- `POST /api/collector/pickups/{id}/verification`
- `PUT /api/collector/pickups/{id}/verification`

---

### 7. Module 2: Final Price Calculation, Transaction & Payment

#### [MODIFY] [`Transaction.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/entity/Transaction.java) & [`Payment.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/entity/Payment.java)
- Transaction: transactionId, pickup, user, collector, category, actualWeight, appliedRate, finalAmount, status (`PENDING`, `COMPLETED`, `CANCELLED`), completedAt.
- Final price calculation: $\text{actualWeight} \times \text{configuredRatePerKg}$ evaluated server-side.
- Payment: paymentId, transaction, amount, method (`WALLET`, `CASH`, `UPI`), status (`PENDING`, `PROCESSING`, `SUCCESS`, `FAILED`, `REFUNDED`), providerReference.
- Pluggable `PaymentProvider` abstraction with `MockPaymentProvider`.

#### [MODIFY] [`TransactionRepository.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/repository/TransactionRepository.java) & [`PaymentRepository.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/repository/PaymentRepository.java)

#### [NEW] `TransactionService.java` & [MODIFY] [`PaymentService.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/service/PaymentService.java)
- Atomic transaction completion:
  - Verifies pickup state, collector verification, rate from `PricingService`.
  - Calculates final amount with `BigDecimal`.
  - Sets transaction to `COMPLETED`, pickup to `COMPLETED`.
  - Credits user wallet with transaction amount.
  - Automatically initializes initial `RecyclingRecord` and `QRRecord`.
  - Emits `TransactionCompletedEvent` via Spring `ApplicationEventPublisher` (allowing Module 3 to subscribe in the future without coupling).
  - Idempotent and thread-safe.

#### [MODIFY] [`PaymentController.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/controller/PaymentController.java) & [NEW] `TransactionController.java`
- `GET /api/transactions`
- `GET /api/transactions/{id}`
- `POST /api/transactions/{id}/complete`
- `GET /api/transactions/{id}/receipt`
- `POST /api/payments`
- `GET /api/payments/{id}`
- `GET /api/payments/{id}/status`

---

### 8. Module 2: Wallet Operations

#### [NEW] `Wallet.java` & `WalletTransaction.java`
- Wallet: user (OneToOne), balance (`BigDecimal`), currency (`INR`), timestamps.
- WalletTransaction: wallet, type (`CREDIT`, `DEBIT`), amount (`BigDecimal`), referenceType (`TRANSACTION`), referenceId, status, timestamp.
- Strict anti-tampering rule: No frontend balance modification API. Balance can only change through trusted backend domain events (e.g. transaction completion).

#### [NEW] `WalletRepository.java` & `WalletTransactionRepository.java`

#### [MODIFY] [`WalletService.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/service/WalletService.java) & [`WalletController.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/controller/WalletController.java)
- `GET /api/wallet`
- `GET /api/wallet/balance`
- `GET /api/wallet/transactions`
- `GET /api/wallet/transactions/{id}`

---

### 9. Module 2: Recycling & Traceability Lifecycle

#### [MODIFY] [`RecyclingRecord.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/entity/RecyclingRecord.java)
- Fields: transaction, pickup, wasteCategory, wasteItem, weight, collector, recycler, handoverInfo, status (`COLLECTED`, `SORTED`, `AGGREGATED`, `TRANSPORT`, `RECEIVED`, `PROCESSING`, `RECYCLED`), processingInfo, timestamps.
- Lifecycle state transitions validated:
  - `COLLECTED` $\to$ `SORTED` $\to$ `AGGREGATED` $\to$ `TRANSPORT` $\to$ `RECEIVED` $\to$ `PROCESSING` $\to$ `RECYCLED`.
  - Rejects backwards transitions.

#### [MODIFY] [`RecyclingService.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/service/RecyclingService.java)
- Recycler authorization checks: only authorized recyclers can receive and process.
- Detailed timeline generation for users to trace their recycled items.

#### [MODIFY] [`RecyclingController.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/controller/RecyclingController.java) & [`RecyclerController.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/controller/RecyclerController.java)
- User endpoints:
  - `GET /api/recycling`
  - `GET /api/recycling/{id}`
  - `GET /api/recycling/{id}/timeline`
- Recycler endpoints:
  - `GET /api/recycler/incoming`
  - `GET /api/recycler/records`
  - `POST /api/recycler/records/{id}/receive`
  - `PUT /api/recycler/records/{id}/status`
  - `POST /api/recycler/records/{id}/processing`
  - `POST /api/recycler/records/{id}/complete`

---

### 10. Module 2: QR Traceability

#### [MODIFY] [`TraceabilityRecord.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/entity/TraceabilityRecord.java)
- Stores QR code mapping, transaction reference, public payload JSON (category, weight, status, timestamps).
- Strictly sanitized: no passwords, tokens, phone numbers, or payment secrets.

#### [MODIFY] [`QRService.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/service/QRService.java)
- ZXing barcode generation: produces standard QR PNG as Base64 data URL or byte array.
- Lookup by QR code or transaction ID.

#### [MODIFY] [`QRController.java`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/controller/QRController.java)
- `POST /api/qr/generate/{transactionId}`
- `GET /api/qr/transaction/{transactionId}`
- `GET /api/qr/{code}`

---

### 11. Database Flyway Migrations

- [`V1__create_users.sql`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/resources/db/migration/V1__create_users.sql): Baseline users, roles, collectors, recyclers, notifications.
- [`V2__create_waste.sql`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/resources/db/migration/V2__create_waste.sql): Waste categories, waste items, AI predictions, initial seeded categories & pricing.
- [`V3__create_pickups.sql`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/resources/db/migration/V3__create_pickups.sql): Pickups, pickup status history, waste verifications with indexes on user, collector, status.
- [`V4__create_transactions.sql`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/resources/db/migration/V4__create_transactions.sql): Transactions, payments, wallets, wallet transactions with unique constraints.
- [`V6__create_recycling.sql`](file:///d:/Projects/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/resources/db/migration/V6__create_recycling.sql): Recycling records, QR traceability records.

---

## Verification Plan

### Automated Tests
Run via Maven:
```powershell
& "C:\Program Files\JetBrains\IntelliJ IDEA 2026.1.3\plugins\maven\lib\maven3\bin\mvn.cmd" clean test -f "d:\Projects\Auronyx\KABADIWALA\backend\kabadiwala-backend\pom.xml"
```

Unit and Integration Tests to implement:
1. `WasteServiceTest`: category listing, active filter, lookup.
2. `PricingServiceTest`: `BigDecimal` calculation, rate lookup, invalid weight handling.
3. `AIServiceTest`: upload validation (size, MIME type), response mapping, fallback mock on service failure.
4. `PickupServiceTest`: creation, status transition state machine, cancellation rules, unauthorized user access prevention.
5. `CollectorOperationsTest`: nearby pickup discovery, acceptance, rejection, status updates.
6. `WasteVerificationTest`: physical verification, positive weight validation, duplicate prevention.
7. `TransactionServiceTest`: complete flow, price recalculation on backend, missing verification rejection, idempotency.
8. `PaymentServiceTest`: payment creation, status tracking, amount immutability.
9. `WalletServiceTest`: balance credit on transaction, anti-duplicate credits, balance retrieval.
10. `RecyclingServiceTest`: full lifecycle transitions (`COLLECTED` $\to$ `RECYCLED`), timeline generation.
11. `QRServiceTest`: QR code generation, lookup, absence of sensitive data.
12. `SecurityPriceTamperingTest`: explicit attack simulation testing that client-supplied `finalAmount` or `balance` is rejected/ignored.

### Build Verification
Run full Maven package:
```powershell
& "C:\Program Files\JetBrains\IntelliJ IDEA 2026.1.3\plugins\maven\lib\maven3\bin\mvn.cmd" package -DskipTests=false -f "d:\Projects\Auronyx\KABADIWALA\backend\kabadiwala-backend\pom.xml"
```
Ensure all tests pass and `jar` is successfully produced.
