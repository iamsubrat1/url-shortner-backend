# 🚀 Distributed URL Shortener Backend

A scalable and secure URL Shortener backend built with Spring Boot, featuring stateless authentication and distributed rate limiting.

---

## 📌 Overview

This service provides secure URL shortening APIs designed for horizontal scalability and abuse protection. The architecture emphasizes stateless security, distributed rate limiting, and performance-aware data modeling.

---

# 🏗️ Architecture

```
                ┌──────────────┐
                │    Client    │
                └───────┬──────┘
                        │
                        ▼
        ┌─────────────────────────────┐
        │  Security Filter Chain      │
        │  - JWT Authentication       │
        │  - AuthenticationEntryPoint │
        └──────────────┬──────────────┘
                       │
                       ▼
        ┌─────────────────────────────┐
        │ RateLimitingFilter          │
        │ - Redis Sliding Window      │
        │ - Per-user Isolation        │
        └──────────────┬──────────────┘
                       │
                       ▼
        ┌─────────────────────────────┐
        │ Controller Layer (/api/v1)  │
        └──────────────┬──────────────┘
                       │
                       ▼
        ┌─────────────────────────────┐
        │ Service Layer               │
        └──────────────┬──────────────┘
                       │
                       ▼
        ┌─────────────────────────────┐
        │ Redis + Database (Planned)  │
        └─────────────────────────────┘
```

---

# ✅ Implemented Features

### 🔐 Stateless JWT Authentication
- Custom `JwtAuthenticationFilter`
- Custom `AuthenticationEntryPoint`
- All endpoints secured except `/api/v1/auth/**`
- Proper HTTP semantics:
    - `401` → Unauthenticated
    - `403` → Forbidden
- Fully stateless (no server-side sessions)

---

### 🚦 Per-User Distributed Rate Limiting
- Sliding Window algorithm implemented using Redis (Memurai)
- Redis key format:

```
rate_limit:user:{username}
```

- Atomic operations via Lua
- TTL-managed window cleanup
- True multi-user isolation
- Safe for horizontal scaling across multiple instances

---

### 🗂 Versioned APIs
All endpoints are exposed under:

```
/api/v1/**
```

Enables backward-compatible API evolution.

---

### 📊 Observability
- Spring Boot Actuator enabled
- Health endpoints exposed
- Metrics-ready for Prometheus integration

---

### ⚡ Performance-Aware Design
- Indexed short URL column for optimized lookup performance
- Designed for read-heavy redirect workloads
- Clean separation of controller, service, and infrastructure layers

---

# 🧠 Engineering Decisions

### Why JWT?
- Stateless and horizontally scalable
- No session replication required
- Clean separation of authentication layer

### Why Redis for Rate Limiting?
- O(log N) sorted set operations
- Native TTL support
- High throughput under concurrent load
- Distributed consistency without in-memory locks

### Why Sliding Window?
- More accurate than fixed window
- Prevents burst abuse at window boundaries

---

# 🚀 Roadmap

### Authentication & Authorization
- Database-backed user authentication
- BCrypt password hashing
- UUID-based identity
- Role-based authorization

### Persistence Layer
- PostgreSQL/MySQL integration
- Optimized indexing strategy
- Soft delete support
- URL expiration handling

### Caching
- Redis caching for short → long URL resolution
- Reduced database read pressure
- Lower redirect latency

### Testing
- Unit tests (service layer)
- Integration tests (security + controller)
- TestContainers for Redis & DB
- Edge-case rate limiting validation

### Observability Enhancements
- Prometheus metrics
- Rate-limit dashboards
- Structured logging
- Request tracing

### Advanced Features
- Per-role rate limits
- Retry-After header in 429 responses
- Click analytics
- Distributed ID generation (Snowflake)
- URL expiration policies

---

# 🛠️ Tech Stack

- Java 17+
- Spring Boot
- Spring Security
- Redis (Memurai)
- JWT
- Maven

---

# 🏃 Running the Application

### 1️⃣ Start Redis (Memurai)

```
localhost:6379
```

### 2️⃣ Start the application

```
mvn spring-boot:run
```

### 3️⃣ Authenticate

```
POST /api/v1/auth/login?username=test
```

Use the returned token:

```
Authorization: Bearer <token>
```

---

## Maintainer

Subrat Kumar Sahoo