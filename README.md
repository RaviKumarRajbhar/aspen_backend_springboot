# Aspen - Social Media Backend

Aspen is a backend system for a social media application built using Java and Spring Boot.

This project focuses on secure authentication, scalable backend architecture, caching, token management, and social media features like posts, comments, likes, follows, and timelined feeds.

---

> 🚧 Project Status: Under Active Development

## Features

### Authentication & Security
- User Registration
- User Login
- JWT Authentication
- Access Token Authentication
- Refresh Token Authentication
- Google OAuth Login
- Password Encryption using Spring Security
- Protected Routes

---

## Social Media Features
- Create Post
- Like / Unlike Posts
- Comment on Posts
- Follow / Unfollow Users
- Timelined User Feed

---

## Architecture
This project follows layered architecture:

- Controller Layer
- Service Layer
- Repository Layer
- DTO Layer
- Global Exception Handling

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

---

## Testing

Testing is implemented using:

- JUnit
- Mockito

Test coverage includes:

- Authentication Logic
- Service Layer Testing

---

## Project Modules

### Auth Module
Handles:
- Register
- Login
- Token Generation
- Token Refresh
- Google OAuth Login

### User Module
Handles:
- User Profile
- User Information

### Post Module
Handles:
- Create Post
- Fetch Posts

### Like Module
Handles:
- Like / Unlike functionality

### Comment Module
Handles:
- Add Comments

### Follow Module
Handles:
- Follow / Unfollow users

### Feed Module
Handles:
- Timelined User feed

---

## Environment Variables

Set the following environment variables before running:

```env
aspen_backend_db_url=
aspen_backend_db_username=
aspen_backend_db_password=
aspen_backend_jwt_secret=
aspen_backend_db_redis_host=
aspen_backend_web_client_id=
