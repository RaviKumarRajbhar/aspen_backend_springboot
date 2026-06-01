# Aspen - Social Media Backend

Aspen is a backend system for a social media application built using Java and Spring Boot.

This project focuses on secure authentication, scalable backend architecture, caching, token management, and social media features like posts, comments, likes, follows, and timelined feeds.

---

> 🚧 Project Status: Under Active Development

## Features

### Authentication & Security
- Email OTP Based User Registration
- User Login
- JWT Authentication
- Refresh Token Authentication
- Google OAuth Login
- Password Encryption using Spring Security
- Protected Routes
- Redis Based Rate Limiting (Fixed Window)
- Redis Based Password Reset System



---

## Social Media Features
- Create Post
- Like / Unlike Posts
- Comment on Posts
- Follow / Unfollow Users
- Following-based feed generation
- Notifications for Likes
- Notifications for Follows
- Notifications for Comments
- Get User Notifications
- Paginated Feed
- Paginated User Posts
- Paginated Comments
- Paginated Followers & Following
- Real-time Push Notification using Firebase FCM
- Multi-device Notification Support
- Device Token Registration & Management
- JWT Secured WebSocket Authentication
- One-to-One Messaging
- Chat History Retrieval
- Cursor Based Chat Pagination
- Conversation List API
- Unread Message Count
- Message Status Tracking (SENT / DELIVERED / SEEN)
- Offline Message Delivery
- Online Presence Tracking
- Last Seen Tracking

---

## Architecture
This project follows layered architecture:

- Controller Layer
- Service Layer
- Repository Layer
- DTO Layer
- Global Exception Handling
- RESTful API Design
- Pagination
- Redis Based Rate Limiting (Fixed Window)
- Multi-device Push Notification Infrastructure
- Composite Database Indexing
- Query Optimization for Feed & Notifications
- Asynchronous Email Sending
- Asynchronous Notification Sending
- WebSocket Based Real-Time Communication

### Performance Optimizations
- Redis Based User Caching
- Database Query Optimization using Composite Indexes
- Feed Query Optimization
- Notification Query Optimization
- Optimized Pagination Queries

---

## Tech Stack

- Java 21
- Spring Boot
- Spring Security
- JWT Authentication
- Google OAuth
- PostgreSQL
- Redis
- JPA / Hibernate
- Gradle
- Gmail SMTP
- Firebase Cloud Messaging (FCM)
- Spring WebSocket

---
## Redis Usage

Redis is used for:

- OTP Storage
- Refresh Token Management
- Password Reset Token Storage
- API Rate Limiting
- User Response Caching

---

## Testing

Testing is implemented using:

- JUnit
- Mockito
- Unit Testing

Test coverage includes:

- Authentication Logic
- Auth Service Layer Testing
- Post Service Layer Testing

---

## Project Modules

### Auth Module
Handles:
- OTP Registration
- OTP Verification
- Login
- Token Generation
- Token Refresh
- Google OAuth Login
- Forgot Password
- Password Reset via Email Token

### User Module
Handles:
- User Profile
- User Information

### Post Module
Handles:
- Create Post
- Fetch Posts
- Paginated User Posts

### Like Module
Handles:
- Like / Unlike functionality

### Comment Module
Handles:
- Add Comments
- Paginated Comments

### Follow Module
Handles:
- Follow / Unfollow users
- Paginated Followers & Following

### Feed Module
Handles:
- Following-based feed generation
- Displays posts from followed users
- Feed Pagination
- Optimized Feed Queries using Composite Indexes

### Chat Module

Handles:

- One-to-One Messaging
- WebSocket Authentication
- Message Persistence
- Chat History
- Cursor Pagination
- Conversation List
- Unread Message Count
- Message Status Management
- Online Presence Tracking
- Last Seen Tracking

### Notification Module
Handles:
- Like Notifications
- Comment Notifications
- Follow Notifications
- Paginated Notification Fetching
- Persistent Notification Storage

## Notification Flow

Aspen supports real-time notification delivery using Firebase Cloud Messaging (FCM).

Notification workflow:

1. User performs action (Like / Comment / Follow)
2. Notification entity stored in PostgreSQL
3. Receiver device tokens fetched
4. Push notification sent to all active devices

Supports:
- Multi-device notification delivery
- Notification persistence
- Real-time push delivery

---

## Environment Variables

Set the following environment variables before running:

```env
aspen_backend_db_url=
aspen_backend_db_username=
aspen_backend_db_password=
aspen_backend_jwt_secret=
aspen_backend_redis_host=
aspen_backend_redis_port=
aspen_backend_web_client_id=
aspen_mail=
aspen_mail_password=
```

## Running with Docker

Start PostgreSQL and Redis containers:

```bash
docker compose up -d
```

Verify running containers:

```bash
docker ps
```

## Local Setup

1. Clone repository
2. Configure environment variables
3. Add Firebase Admin SDK JSON file
4. Start PostgreSQL & Redis
5. Run application