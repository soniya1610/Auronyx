# Implementation Plan - Module 1: Core & User Module (Kabadiwala Backend)

This plan details the full implementation of **MODULE 1 — CORE & USER MODULE** for the Kabadiwala backend. It implements all 10 required submodules, strict security and role-based access control, Flyway database migrations, and a comprehensive test suite without implementing business logic belonging to Module 2 (Waste Operations) or Module 3 (Rewards & Ecosystem).

## User Review Required

> [!IMPORTANT]
> - The existing project files in `KABADIWALA/backend/kabadiwala-backend` (including `pom.xml`, source files, and SQL files) are currently empty templates (0 bytes). We will populate them cleanly according to standard Spring Boot 3.3.x / Java 17 architecture.
> - An external MySQL server is currently not running locally on port 3306. The application is configured to connect to MySQL via configurable environment variables (`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`), and includes an H2 in-memory MySQL-compatible configuration for testing (`mvn test`) so that automated tests and CI run cleanly without requiring an external MySQL daemon.
> - Public registration strictly assigns the `USER` role. Accounts with `COLLECTOR` or `RECYCLER` roles can be provisioned through secure role onboarding or administrative assignment.

## Submodules in Scope

1. **Common / Core**: API response format (`ApiResponse<T>`), error handling (`ErrorResponse`, `GlobalExceptionHandler`), custom exceptions (`ResourceNotFoundException`, `UnauthorizedException`, `ForbiddenException`), enums (`RoleType`, `OtpPurpose`, `NotificationType`).
2. **Authentication**: `POST /api/auth/register`, `POST /api/auth/login`, `POST /api/auth/logout`.
3. **OTP Authentication**: `POST /api/auth/send-otp`, `POST /api/auth/verify-otp` (generation, 5-minute expiry, max 3 attempts, mock local provider, secure invalidation).
4. **Password Reset**: `POST /api/auth/forgot-password`, `POST /api/auth/reset-password` (integrated with verified OTP).
5. **Role & JWT Security**: Stateless JWT authentication, `JwtAuthenticationFilter`, `CustomUserDetailsService`, `SecurityConfig`, `SecurityUtils`, BCrypt hashing, 401/403 handlers.
6. **User Management**: `GET /api/users/me`, `PUT /api/users/me`, `PUT /api/users/me/location`, `PUT /api/users/me/language`, `GET /api/users/me/dashboard`, `GET /api/users/me/impact`.
7. **Collector Profile & Discovery**: `GET /api/collector/profile`, `PUT /api/collector/profile`, `PUT /api/collector/availability`, `GET /api/collectors`, `GET /api/collectors/{id}`, `GET /api/collectors/nearby` (Haversine formula based on actual stored coordinates).
8. **Recycler Profile**: `GET /api/recycler/profile`, `PUT /api/recycler/profile`.
9. **Notification Module**: `GET /api/notifications`, `GET /api/notifications/unread`, `PUT /api/notifications/{id}/read`, `PUT /api/notifications/read-all`, `DELETE /api/notifications/{id}`, reusable `NotificationService`.
10. **System Health & Application Configuration**: `GET /api/health`, `GET /api/config`, `GET /api/constants` (safe, non-sensitive configuration only).

---

## Proposed Changes

### Build and Configuration

#### [MODIFY] [pom.xml](file:///c:/Users/kumar/Auronyx/KABADIWALA/backend/kabadiwala-backend/pom.xml)
- Define Spring Boot 3.3.4 parent, Java 17, and dependencies:
  - `spring-boot-starter-web`
  - `spring-boot-starter-security`
  - `spring-boot-starter-data-jpa`
  - `spring-boot-starter-validation`
  - `flyway-core`, `flyway-mysql`
  - `mysql-connector-j`
  - `jjwt-api`, `jjwt-impl`, `jjwt-jackson` (v0.12.5)
  - `h2` (test scope / MySQL compatibility)
  - `spring-boot-starter-test`, `spring-security-test`
- Configure `maven-compiler-plugin` and `spring-boot-maven-plugin`.

#### [MODIFY] [application.properties](file:///c:/Users/kumar/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/resources/application.properties)
- Configure datasource with environment variable fallback:
  - `spring.datasource.url=jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:kabadiwala_db}?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC`
  - `spring.datasource.username=${DB_USERNAME:root}`
  - `spring.datasource.password=${DB_PASSWORD:}`
  - `spring.jpa.hibernate.ddl-auto=validate`
  - `spring.flyway.enabled=true`
  - `jwt.secret=${JWT_SECRET:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}`
  - `jwt.expiration=${JWT_EXPIRATION:86400000}` (24 hours)
  - `cors.allowed-origins=${CORS_ALLOWED_ORIGINS:http://localhost:3000,http://localhost:5173}`
  - `app.otp.expiration-minutes=5`
  - `app.otp.max-attempts=3`

#### [NEW] [application-test.properties](file:///c:/Users/kumar/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/test/resources/application-test.properties)
- Configure in-memory H2 database with `MODE=MySQL` and disabled Flyway for fast, isolated, deterministic unit/integration test runs.

---

### Database Migrations

#### [MODIFY] [V1__create_users.sql](file:///c:/Users/kumar/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/resources/db/migration/V1__create_users.sql)
- Create initial schema tables for Module 1:
  - `roles` (id, name: ROLE_USER, ROLE_COLLECTOR, ROLE_RECYCLER, ROLE_ADMIN)
  - `users` (id, name, email, phone, password, address, city, state, pincode, latitude, longitude, preferred_language, enabled, created_at, updated_at)
  - `user_roles` (user_id, role_id)
  - `otps` (id, target, otp_code, purpose, expires_at, verified, attempts, created_at)
  - `collectors` (id, user_id, vehicle_type, vehicle_number, service_area, address, city, state, pincode, latitude, longitude, is_available, is_active, verification_status, working_hours, created_at, updated_at)
  - `recyclers` (id, user_id, organization_name, business_type, gst_number, address, city, state, pincode, latitude, longitude, is_active, verification_status, created_at, updated_at)
  - `notifications` (id, user_id, title, message, type, is_read, created_at, read_at)
  - Default role seeding: `ROLE_USER`, `ROLE_COLLECTOR`, `ROLE_RECYCLER`, `ROLE_ADMIN`.

---

### Common & Exception Handling

#### [NEW] [ApiResponse.java](file:///c:/Users/kumar/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/dto/ApiResponse.java)
- Standardized response envelope: `success` (boolean), `message` (String), `data` (T), `timestamp` (Instant).

#### [NEW] [ErrorResponse.java](file:///c:/Users/kumar/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/dto/ErrorResponse.java)
- Standardized error format: `status` (int), `error` (String), `message` (String), `validationErrors` (Map<String, String>), `timestamp` (Instant).

#### [MODIFY] [GlobalExceptionHandler.java](file:///c:/Users/kumar/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/exception/GlobalExceptionHandler.java)
- Handlers for:
  - `ResourceNotFoundException` -> 404
  - `UnauthorizedException` and `BadCredentialsException` -> 401
  - `ForbiddenException` and `AccessDeniedException` -> 403
  - `MethodArgumentNotValidException` -> 400 (with field validation errors map)
  - `IllegalArgumentException` / `IllegalStateException` -> 400
  - General `Exception` -> 500 (safe message, no stack trace leak)

#### [MODIFY] [ResourceNotFoundException.java](file:///c:/Users/kumar/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/exception/ResourceNotFoundException.java)
#### [MODIFY] [UnauthorizedException.java](file:///c:/Users/kumar/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/exception/UnauthorizedException.java)
#### [NEW] [ForbiddenException.java](file:///c:/Users/kumar/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/exception/ForbiddenException.java)

---

### Domain Entities

#### [MODIFY] [Role.java](file:///c:/Users/kumar/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/entity/Role.java)
- Entity mapping to `roles` table with `RoleType` enum (`ROLE_USER`, `ROLE_COLLECTOR`, `ROLE_RECYCLER`, `ROLE_ADMIN`).

#### [MODIFY] [User.java](file:///c:/Users/kumar/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/entity/User.java)
- Entity mapping with all requested fields, `@ManyToMany` with `Role`, timestamps, latitude/longitude, preferredLanguage.

#### [NEW] [OtpToken.java](file:///c:/Users/kumar/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/entity/OtpToken.java)
- Entity mapping for OTP persistence, expiration tracking, attempt count, and consumed status.

#### [MODIFY] [Collector.java](file:///c:/Users/kumar/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/entity/Collector.java)
- Entity mapping to `collectors` table linked to `User`.

#### [MODIFY] [Recycler.java](file:///c:/Users/kumar/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/entity/Recycler.java)
- Entity mapping to `recyclers` table linked to `User`.

#### [MODIFY] [Notification.java](file:///c:/Users/kumar/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/entity/Notification.java)
- Entity mapping to `notifications` table with `NotificationType`, `isRead`, and timestamps.

---

### Repositories

#### [MODIFY] [UserRepository.java](file:///c:/Users/kumar/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/repository/UserRepository.java)
- `findByEmail(String email)`, `findByPhone(String phone)`, `existsByEmail(String email)`, `existsByPhone(String phone)`.

#### [NEW] [RoleRepository.java](file:///c:/Users/kumar/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/repository/RoleRepository.java)
- `findByName(RoleType name)`.

#### [NEW] [OtpRepository.java](file:///c:/Users/kumar/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/repository/OtpRepository.java)
- `findTopByTargetAndPurposeOrderByCreatedAtDesc(String target, OtpPurpose purpose)`.

#### [MODIFY] [CollectorRepository.java](file:///c:/Users/kumar/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/repository/CollectorRepository.java)
- `findByUserId(Long userId)`, `findAllByIsActiveTrue()`, proximity query or service-level Haversine calculation.

#### [MODIFY] [RecyclerRepository.java](file:///c:/Users/kumar/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/repository/RecyclerRepository.java)
- `findByUserId(Long userId)`, `findAllByIsActiveTrue()`.

#### [MODIFY] [NotificationRepository.java](file:///c:/Users/kumar/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/repository/NotificationRepository.java)
- `findByUserIdOrderByCreatedAtDesc(Long userId)`, `findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId)`, `countByUserIdAndIsReadFalse(Long userId)`.

---

### Security & JWT

#### [MODIFY] [JwtService.java](file:///c:/Users/kumar/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/security/JwtService.java)
- Token generation with claims (email, roles, userId), token validation, username extraction, expiration checks using JJWT 0.12.5.

#### [MODIFY] [JwtAuthenticationFilter.java](file:///c:/Users/kumar/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/security/JwtAuthenticationFilter.java)
- Intercept HTTP requests, parse Bearer token, validate, set `SecurityContextHolder`.

#### [MODIFY] [CustomUserDetailsService.java](file:///c:/Users/kumar/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/security/CustomUserDetailsService.java)
- Load user by email/username and map authorities from assigned roles.

#### [MODIFY] [SecurityUtils.java](file:///c:/Users/kumar/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/security/SecurityUtils.java)
- Retrieve currently authenticated user's email/id from `SecurityContext`.

#### [MODIFY] [SecurityConfig.java](file:///c:/Users/kumar/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/config/SecurityConfig.java)
- Security filter chain, stateless session, URL authorization rules:
  - Public: `/api/auth/**`, `/api/health`, `/api/config`, `/api/constants`, `/api/collectors`, `/api/collectors/**` (read-only discovery).
  - Authenticated: `/api/users/me/**`, `/api/notifications/**`.
  - Collector only: `/api/collector/**` (`hasRole('COLLECTOR')`).
  - Recycler only: `/api/recycler/**` (`hasRole('RECYCLER')`).
- Custom `AuthenticationEntryPoint` (401) and `AccessDeniedHandler` (403).

#### [MODIFY] [CorsConfig.java](file:///c:/Users/kumar/Auronyx/KABADIWALA/backend/kabadiwala-backend/src/main/java/com/kabadiwala/config/CorsConfig.java)
- Configured from `cors.allowed-origins` property.

---

### DTOs

- `RegisterRequest`: name, email, phone, password.
- `LoginRequest`: email, password.
- `AuthResponse`: token, tokenType, user (id, name, email, phone, role).
- `OTPRequest`: target (email/phone), purpose (REGISTRATION_VERIFICATION, PASSWORD_RESET).
- `OTPVerificationRequest`: target, otp, purpose.
- `ForgotPasswordRequest`: target (email).
- `ResetPasswordRequest`: target, otp, newPassword.
- `UserResponse`: id, name, email, phone, role, address, city, state, pincode, latitude, longitude, preferredLanguage, enabled, createdAt, updatedAt.
- `UserUpdateRequest`: name, address, city, state, pincode.
- `LocationUpdateRequest`: address, city, state, pincode, latitude (-90..90), longitude (-180..180).
- `LanguageUpdateRequest`: language (e.g. en, hi).
- `UserDashboardResponse`: user info, role, unreadNotificationsCount, walletSummary (placeholder), pointsSummary (placeholder), upcomingPickup (placeholder).
- `UserImpactResponse`: default zero values placeholder for future Module 2/3.
- `CollectorResponse`, `CollectorProfileUpdateRequest`, `CollectorAvailabilityRequest`.
- `RecyclerResponse`, `RecyclerProfileUpdateRequest`.
- `NotificationResponse`.
- `HealthResponse`: status, timestamp, database.
- `ConfigResponse`: appName, version, supportedLanguages, env.

---

### Services

- `AuthService`: registration (hashes password, assigns ROLE_USER, checks uniqueness), login (authenticates, generates JWT), logout.
- `OTPService`: generates 6-digit OTP, saves with expiration, validates with attempt counting, invalidates upon success/expiry.
- `UserService`: manages profile, location, language, dashboard summary, impact placeholder.
- `CollectorService`: authenticated collector profile update, availability toggle, public collector discovery, nearby search with Haversine distance.
- `RecyclerService`: authenticated recycler profile update and fetch.
- `NotificationService`: list user notifications, unread count, mark read, mark all read, delete, plus reusable `createNotification` method.

---

### Controllers

- `AuthController`: `/api/auth/*`
- `UserController`: `/api/users/me/*`
- `CollectorController`: `/api/collectors/*` and `/api/collector/*`
- `RecyclerController`: `/api/recycler/*`
- `NotificationController`: `/api/notifications/*`
- `SystemController` (or inside `KabadiwalaApplication` / `CommonController`): `/api/health`, `/api/config`, `/api/constants`

---

## Verification Plan

### Automated Tests
Run via Maven:
```bash
./mvnw clean test
# or
mvn clean test
```
The test suite will cover:
1. `AuthServiceTest` / `AuthControllerTest`:
   - Registration with validation (valid email, valid phone, password hashing).
   - Duplicate email and duplicate phone rejection.
   - Forbidding registration with elevated roles directly.
   - Login success with valid credentials (JWT returned).
   - Login failure with bad credentials (401 response).
2. `OTPServiceTest`:
   - OTP generation and storage.
   - OTP verification success and subsequent consumption.
   - Expiration verification.
   - Maximum attempt enforcement (invalidation after threshold).
   - Password reset flow via OTP.
3. `UserServiceTest` / `UserControllerTest`:
   - Get authenticated user profile (`/api/users/me`).
   - Profile update with valid fields (cannot change role or password hash).
   - Location update with latitude/longitude boundary validation.
   - Language update.
   - User dashboard and impact responses.
4. `CollectorServiceTest` / `CollectorControllerTest`:
   - Collector profile retrieval and update for authenticated collector.
   - Collector availability update.
   - Proximity search `/api/collectors/nearby` with real distance calculation.
   - Forbidden access for non-collectors.
5. `RecyclerServiceTest` / `RecyclerControllerTest`:
   - Recycler profile retrieval and update for authenticated recycler.
   - Forbidden access for non-recyclers.
6. `NotificationServiceTest` / `NotificationControllerTest`:
   - User notification retrieval and unread filtering.
   - Marking notification as read with ownership validation.
   - Marking all read.
   - Deleting notification with ownership check.
   - Reusable `createNotification` service method.
7. `SecurityIntegrationTest`:
   - Accessing protected endpoints without token returns 401.
   - Accessing role-restricted endpoints with wrong role returns 403.
   - Tampered/invalid JWT returns 401.
8. `SystemEndpointsTest`:
   - `/api/health` returns status UP and safe details.
   - `/api/config` and `/api/constants` return safe frontend config without leaking secrets.

### Build Package Verification
```bash
mvn package -DskipTests=false
```
Verify the JAR packages cleanly and starts up.
