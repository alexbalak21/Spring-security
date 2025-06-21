# Spring Security Project Documentation

## Overview
This project demonstrates a secure REST API built with Spring Boot and Spring Security. It implements JWT (JSON Web Token) based authentication and role-based authorization.

## Table of Contents
- [Project Structure](#project-structure)
- [Features](#features)
- [API Endpoints](#api-endpoints)
- [Authentication](#authentication)
- [Security Configuration](#security-configuration)
- [Getting Started](#getting-started)
- [Testing](#testing)
- [Dependencies](#dependencies)
- [Contributing](#contributing)

## Project Structure

```
src/main/java/app/spring/
├── config/               # Configuration classes
│   └── SecurityConfig.java  # Security configuration
├── controller/           # REST controllers
│   ├── AuthController.java  # Authentication endpoints
│   └── HomeController.java  # Protected endpoints
├── dto/                  # Data Transfer Objects
│   └── LoginRequest.java    # Login request DTO
└── Application.java      # Main application class
```

## Features
- JWT-based authentication
- Role-based authorization (ADMIN role implemented)
- Stateless session management
- Secure password hashing with BCrypt
- In-memory user store (for demonstration purposes)

## API Endpoints

### Authentication
- `POST /api/login`
  - Authenticates a user and returns user details
  - Request body: `{ "username": "string", "password": "string" }`
  - Response: User details including roles

### Protected Endpoints
These endpoints require a valid JWT token in the Authorization header.

- `GET /api/home`
  - A sample protected endpoint
  - Requires authentication
  - Returns a welcome message

## Authentication

### Login Flow
1. Client sends a POST request to `/api/login` with username and password
2. Server validates credentials
3. If valid, returns user details including roles
4. Client stores the JWT token and includes it in subsequent requests in the `Authorization` header

### Security Headers
- `Authorization: Bearer <token>` - Required for protected endpoints

## Security Configuration

The security is configured in `SecurityConfig.java` with the following settings:

- CSRF protection is disabled (as we're using JWT)
- Session management is stateless
- Basic authentication is disabled
- Form login is disabled
- All requests to `/api/login` are permitted
- All other requests require authentication

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

The application will start on `http://localhost:8080`

### Default User
- **Username:** alex
- **Password:** password
- **Role:** ADMIN

## Testing

### Using cURL

1. **Login** (Get JWT token):
   ```bash
   curl -X POST http://localhost:8080/api/login \
     -H "Content-Type: application/json" \
     -d '{"username":"alex","password":"password"}'
   ```

2. **Access protected endpoint** (use the token from login response):
   ```bash
   curl http://localhost:8080/api/home \
     -H "Authorization: Bearer <your-jwt-token>"
   ```

## Dependencies

- Spring Boot 3.5.3
- Spring Security
- Spring Web
- JUnit (for testing)
- Spring Security Test

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
