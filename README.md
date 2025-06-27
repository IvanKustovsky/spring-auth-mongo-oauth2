# 🩺 Health Tracker API

Backend API for tracking personal health metrics using **Spring Boot**, **MongoDB**, and **Google + Basic Authentication**. Built as a learning project with clean architecture and AI-assisted development in **Cursor**.

---

## ⚙️ Technologies (MVP)

- **Java 21**, **Spring Boot 3.5.x**
- **Spring Security** (Google OAuth2 + Basic Auth)
- **MongoDB** (document storage)
- **JWT** (for stateless sessions)
- **Cursor AI IDE** (first-time usage)

---

## 🎯 Features (MVP)

- ✅ User registration with email & password
- ✅ Login with JWT token issuance
- ✅ Login with Google account (OAuth2)
- ✅ MongoDB storage for user accounts
- ⏳ Health metric tracking (next step)
- ⏳ Caching, RabbitMQ, Email (future step)

---

## 📦 MongoDB Document Example

```json
{
  "_id": "user123",
  "email": "ivan@example.com",
  "passwordHash": "$2a$10$...",
  "authProvider": "LOCAL",
  "createdAt": "2025-06-27T10:00:00Z"
}
