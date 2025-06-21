# Spring Security REST API

## Overview
This project demonstrates a secure REST API built with Spring Boot 3.5.3 and Spring Security 6.5.1. It implements form-based authentication with session management and role-based authorization. The API follows RESTful principles and includes comprehensive error handling.

## Table of Contents
- [Features](#features)
- [Project Structure](#project-structure)
- [API Endpoints](#api-endpoints)
- [Authentication](#authentication)
- [Security](#security)
- [Error Handling](#error-handling)
- [Database](#database)
- [Getting Started](#getting-started)
- [Testing](#testing)
- [Dependencies](#dependencies)
- [Contributing](#contributing)

## Features

- **Form-based Authentication**
- Role-based authorization (USER and ADMIN roles)
- Session-based security
- Secure password hashing with BCrypt
- H2 in-memory database with Flyway migrations
- Comprehensive error handling with consistent JSON responses
- Input validation
- RESTful API design
- Logging configuration

## Project Structure

```
src/main/java/app/spring/
├── config/                  # Configuration classes
│   └── SecurityConfig.java     # Security configuration
├── controller/              # REST controllers
│   ├── AuthController.java     # Authentication endpoints
│   ├── HomeController.java     # Protected endpoints
│   └── UserController.java     # User management endpoints
├── dto/                     # Data Transfer Objects
│   ├── LoginRequest.java       # Login request DTO
│   └── RegisterRequest.java    # Registration request DTO
├── entity/                  # JPA entities
│   └── User.java              # User entity
├── exception/               # Exception handling
│   └── GlobalExceptionHandler.java
├── repository/              # Data access layer
│   └── UserRepository.java
├── security/                # Security related classes
│   ├── UserDetailsImpl.java
│   └── UserDetailsServiceImpl.java
└── Application.java         # Main application class
```

## API Endpoints

### Authentication
- `POST /api/auth/login`
  - Authenticates a user and creates a session
  - Request body: `{ "username": "string", "password": "string" }`
  - Response: User details including roles

- `POST /api/auth/register`
  - Registers a new user
  - Request body: `{ "username": "string", "password": "string" }`
  - Response: Created user details
  - Username must be 3-20 characters
  - Password must be 6-40 characters

### Protected Endpoints
- `GET /api/home`
  - Returns a welcome message
  - Requires authentication

- `GET /api/users`
  - Returns list of all users
  - Requires ADMIN role

## Authentication

### Login Flow
1. Client sends a POST request to `/api/auth/login` with username and password
2. Server validates credentials against the database
3. If valid, creates an authenticated session
4. Subsequent requests include the session cookie automatically

### Security Headers
The application includes the following security headers by default:
- `X-Content-Type-Options: nosniff`
- `X-XSS-Protection: 0`
- `Cache-Control: no-cache, no-store, max-age=0, must-revalidate`
- `Pragma: no-cache`
- `X-Frame-Options: DENY`

## Error Handling

The API returns consistent error responses in the format:
```json
{
  "timestamp": "2025-06-21T14:00:00.000",
  "status": 400,
  "error": "Bad Request",
  "code": "VALIDATION_ERROR",
  "message": "Validation failed: username: must be between 3 and 20 characters"
}
```

### Common Error Codes
- `400 BAD_REQUEST` - Invalid request data
- `401 UNAUTHORIZED` - Authentication required
- `403 FORBIDDEN` - Insufficient permissions
- `404 NOT_FOUND` - Resource not found
- `409 CONFLICT` - Resource conflict (e.g., username already exists)
- `500 INTERNAL_SERVER_ERROR` - Server error

## Database

The application uses H2 in-memory database with the following schema:

### Users Table
- `id` BIGINT (PK)
- `username` VARCHAR(255) UNIQUE
- `password` VARCHAR(255)
- `active` BOOLEAN
- `created_at` TIMESTAMP

### User Roles
- Stored in a join table `user_roles`
- Default role: `ROLE_USER`
- Admin role: `ROLE_ADMIN`

## Getting Started

### Prerequisites
- Java 24
- Maven 3.6.3 or later

### Installation

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd Spring-security
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The application will be available at `http://localhost:8080`

### Default Users
- **Admin User**
  - Username: admin
  - Password: admin
  - Roles: ADMIN, USER

- **Regular User**
  - Username: user
  - Password: password
  - Roles: USER

## Testing

### Using HTTP Client (tests.http)
The project includes a `tests.http` file with sample requests for testing the API.

1. **Register a new user**
   ```http
   POST http://localhost:8080/api/auth/register
   Content-Type: application/json
   
   {
     "username": "testuser",
     "password": "password123"
   }
   ```

2. **Login**
   ```http
   POST http://localhost:8080/api/auth/login
   Content-Type: application/json
   
   {
     "username": "testuser",
     "password": "password123"
   }
   ```

3. **Access protected endpoint**
   ```http
   GET http://localhost:8080/api/home
   ```

### Using cURL

1. **Register a new user**
   ```bash
   curl -X POST http://localhost:8080/api/auth/register \
     -H "Content-Type: application/json" \
     -d '{"username":"testuser","password":"password123"}'
   ```

2. **Login**
   ```bash
   curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -c cookies.txt \
     -d '{"username":"testuser","password":"password123"}'
   ```

3. **Access protected endpoint**
   ```bash
   curl http://localhost:8080/api/home -b cookies.txt
   ```

## Dependencies

- **Spring Boot 3.5.3**
- **Spring Security 6.5.1**
- **Spring Web**
- **Spring Data JPA**
- **H2 Database**
- **Flyway** (database migrations)
- **Lombok** (reduces boilerplate code)
- **Spring Boot DevTools** (development tools)
- **Spring Boot Actuator** (application monitoring)
- **JUnit 5** (testing)
- **Spring Security Test**

## Development

### Code Style
- Follows Google Java Style Guide
- 4 spaces for indentation
- Maximum line length: 120 characters

### Logging
- Logging is configured in `application-dev.properties`
- Log level: DEBUG for development, INFO for production
- Logs are written to `logs/application.log`

### Database Console
In development mode, you can access the H2 database console at:
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:securitydb`
- Username: `sa`
- Password: (leave empty)

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
