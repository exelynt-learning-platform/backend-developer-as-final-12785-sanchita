# Resource Booking System - RESTful API

A secure, production-ready RESTful Resource Booking System built with **Spring Boot 3**, **Java 21**, **Spring Security**, **JWT Authentication**, and **MySQL / PostgreSQL / H2 Database**.

---

## Key Features

- 🔐 **JWT Stateless Authentication**: User login via `POST /auth/login` and registration via `POST /auth/register` with BCrypt password hashing.
- 👥 **Role-Based Access Control (RBAC)**:
  - **ADMIN**: Full CRUD permissions on resources, resource types, and all system reservations. Ability to confirm, cancel, or delete reservations.
  - **USER**: Read-only access to available resources and resource types. Ability to create reservations and manage/view **only their own** reservations.
- 📅 **Reservation Management**:
  - Automatic non-overlapping booking conflict validation.
  - Automatic duration-based decimal price calculation (`BigDecimal`).
  - Reservation Status Lifecycle: `PENDING`, `CONFIRMED`, `CANCELLED`.
- 🔍 **Advanced Filtering, Pagination & Sorting**:
  - Dynamic JPA Specification filtering by `status`, `minPrice`, `maxPrice`, `resourceId`, and `userId`.
  - Paged responses using `page` and `size` parameters.
  - Flexible sorting using `sortBy` and `sortDir` (`asc` / `desc`).
- ⚡ **Database Support**: Out-of-the-box configuration for MySQL, PostgreSQL, or in-memory H2 database.
- 📖 **API Documentation**: Interactive Swagger / OpenAPI documentation UI at `/swagger-ui.html`.
- 🌱 **Automated Seed Data**: Automatic initialization of default Admin and User test accounts, along with sample Resource Types and Resources.

---

## Seed Test Accounts

Upon application startup, the following seed accounts are automatically created if they do not exist:

| Role | Email | Password | Permissions |
|------|-------|----------|-------------|
| **ADMIN** | `admin@example.com` | `Admin@123` | Full access to resources, resource types, and all reservations |
| **USER** | `user@example.com` | `User@123` | Read-only resources, view & create own reservations |

---

## Environment Variables & Configuration

Configuration settings in [`application.properties`](file:///c:/Users/admin/Downloads/Resource_Booking_System/src/main/resources/application.properties) can be customized via environment variables:

| Environment Variable | Default Value | Description |
|----------------------|---------------|-------------|
| `DB_URL` | `jdbc:mysql://localhost:3306/resourcemanagement` | JDBC Database connection URL |
| `DB_USERNAME` | `root` | Database username |
| `DB_PASSWORD` | `Sanchita@123` | Database password |
| `DB_DRIVER` | `com.mysql.cj.jdbc.Driver` | JDBC driver class name |
| `JWT_SECRET` | *(256-bit secret key)* | Secret key used for signing JWT tokens |
| `SERVER_PORT` | `8080` | HTTP Port for the application |

### PostgreSQL Example
To run with PostgreSQL, set environment variables:
```bash
export DB_URL="jdbc:postgresql://localhost:5432/resourcemanagement"
export DB_USERNAME="postgres"
export DB_PASSWORD="yourpassword"
export DB_DRIVER="org.postgresql.Driver"
```

---

## Quick Start & Setup

### Prerequisites
- **JDK 17+** (Java 21 recommended)
- **Maven 3.8+** (or use included Maven Wrapper `mvnw` / `mvnw.cmd`)
- **MySQL 8+** or **PostgreSQL 14+** (or use in-memory H2 fallback)

### Build and Run

1. **Clone & Compile**:
   ```bash
   ./mvnw clean compile
   ```

2. **Run Tests**:
   ```bash
   ./mvnw test
   ```

3. **Start Application**:
   ```bash
   ./mvnw spring-boot:run
   ```
   The server will start at `http://localhost:8080`.

---

## API Reference & Endpoints

### 1. Authentication Endpoints

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| `POST` | `/auth/register` | Public | Register a new user (`ROLE_USER`) |
| `POST` | `/auth/login` | Public | Authenticate user & receive JWT token |

#### Login Request (`POST /auth/login`)
```json
{
  "email": "user@example.com",
  "password": "User@123"
}
```

#### Login Response
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "userId": 2,
  "role": "USER"
}
```

---

### 2. Resources Endpoints

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| `GET` | `/resources` | USER, ADMIN | List all available resources |
| `GET` | `/resources/{id}` | USER, ADMIN | Get resource details by ID |
| `POST` | `/resources` | ADMIN | Create a new resource |
| `PUT` | `/resources/{id}` | ADMIN | Update an existing resource |
| `DELETE` | `/resources/{id}` | ADMIN | Delete a resource |

#### Create Resource Request (`POST /resources`)
```json
{
  "name": "Executive Conference Room 1",
  "description": "20-person capacity with 4K display",
  "location": "Building A, Floor 3",
  "resourceTypeId": 1,
  "pricePerHour": 50.00,
  "active": true
}
```

---

### 3. Reservations Endpoints

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| `POST` | `/reservations` | USER, ADMIN | Create a new reservation (identity from JWT) |
| `GET` | `/reservations/my` | USER, ADMIN | View authenticated user's reservations (paged/filtered) |
| `GET` | `/reservations/{id}` | Owner, ADMIN | Get reservation details by ID |
| `GET` | `/reservations` | ADMIN | View all system reservations (paged/filtered) |
| `PATCH` | `/reservations/{id}/cancel` | Owner, ADMIN | Cancel a reservation |
| `PATCH` | `/reservations/{id}/status` | ADMIN | Update reservation status (`CONFIRMED`, `CANCELLED`) |
| `DELETE` | `/reservations/{id}` | ADMIN | Delete a reservation |

#### Create Reservation Request (`POST /reservations`)
```json
{
  "resourceId": 1,
  "startTime": "2026-10-01T10:00:00",
  "endTime": "2026-10-01T12:00:00",
  "notes": "Quarterly Planning Meeting"
}
```

#### Reservation Filtering, Pagination & Sorting Parameters
Query parameters supported on `GET /reservations` (ADMIN) and `GET /reservations/my` (USER):

- `status`: `PENDING`, `CONFIRMED`, `CANCELLED`
- `minPrice`: Minimum price decimal filter (e.g. `10.00`)
- `maxPrice`: Maximum price decimal filter (e.g. `150.00`)
- `page`: Page index (default: `0`)
- `size`: Page size (default: `10`)
- `sortBy`: Field name to sort by (default: `id`, or `startTime`, `price`)
- `sortDir`: Sort direction (`asc` or `desc`, default: `desc`)

**Example Filter Request**:
```http
GET /reservations?status=PENDING&minPrice=20&maxPrice=200&page=0&size=5&sortBy=startTime&sortDir=asc
```

---

## OpenAPI / Swagger Documentation

Interactive API documentation with JWT Authorization testing is available when the application is running:

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON Spec**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

To test authorized endpoints in Swagger UI:
1. Call `POST /auth/login` to obtain your JWT token.
2. Click **Authorize** at the top right of the Swagger UI page.
3. Enter `Bearer <your_jwt_token>` and click **Authorize**.

---

## Verification & Test Results

Run tests using Maven:
```bash
./mvnw test
```
The test suite validates:
1. **Authentication**: Login credentials verification and JWT token generation.
2. **Security & RBAC**: ADMIN vs USER authorization rules on resources and reservations.
3. **Reservations**: Overlapping time validation, price calculation, ownership enforcement, filtering, and pagination.
