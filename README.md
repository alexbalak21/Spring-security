# Spring Security REST API

[![Java](https://img.shields.io/badge/Java-24%2B-blue)](https://www.oracle.com/java/technologies/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-green)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-6.5.1-green)](https://spring.io/projects/spring-security)

A secure REST API built with Spring Boot and Spring Security, featuring form-based authentication, role-based authorization, and comprehensive error handling.

## 📚 [View Full Documentation](DOCUMENTATION.md)

## Features

- 🔐 Form-based authentication
- 👥 Role-based access control (USER and ADMIN roles)
- 🛡️ Secure password hashing with BCrypt
- 📝 Comprehensive error handling
- 🗄️ H2 in-memory database with Flyway migrations
- 🔄 RESTful API design

## Quick Start

### Prerequisites

- Java 24+
- Maven 3.6.3+

### Running the Application

```bash
# Clone the repository
git clone <repository-url>
cd Spring-security

# Build and run
./mvnw spring-boot:run
```

The application will be available at `http://localhost:8080`

### Default Users

| Username | Password | Roles     |
|----------|----------|-----------|
| admin    | admin    | ROLE_ADMIN|
| user     | password | ROLE_USER |

## API Quick Reference

- `POST /api/auth/login` - Authenticate user
- `POST /api/auth/register` - Register new user
- `GET /api/home` - Sample protected endpoint
- `GET /api/users` - List all users (ADMIN only)

For complete API documentation and usage examples, please refer to the [full documentation](DOCUMENTATION.md).

### Secured Endpoints

- `GET /api/admin` - Accessible by ADMIN role
- `GET /api/user` - Accessible by USER role
- `GET /api/public` - Publicly accessible

## Project Structure

```
src/main/java/app/spring/
├── Application.java           # Main application class
├── config/                   # Configuration classes
├── controller/               # REST controllers
├── dto/                      # Data Transfer Objects
├── entity/                   # JPA entities
├── repository/               # JPA repositories
└── security/                 # Security configurations
```

## Database Access

The application uses H2 in-memory database with the following access details:

- **H2 Console URL**: http://localhost:8080/h2-console
- **JDBC URL**: jdbc:h2:mem:securitydb
- **Username**: sa
- **Password**: password

To access the H2 console:
1. Make sure the application is running
2. Open http://localhost:8080/h2-console in your browser
3. Enter the JDBC URL, username, and password from above
4. Click 'Connect'

## Security Configuration

The application uses Spring Security with:
- Form-based authentication
- CSRF protection
- Session management
- Password encoding
- Role-based authorization

## Data Access

The application uses Spring Data JPA with the following features:
- H2 in-memory database
- Automatic schema generation
- JPA repositories
- Entity relationships
- Custom queries

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [Spring Security](https://spring.io/projects/spring-security)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Maven](https://maven.apache.org/)
