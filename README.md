# Spring Security Application

A secure web application built with Spring Boot and Spring Security, demonstrating authentication and authorization features.

## Features

- User authentication with Spring Security
- Role-based access control (RBAC)
- RESTful API endpoints
- Secure password storage
- JWT (JSON Web Token) support (if implemented)
- H2 in-memory database
- Spring Data JPA for data access

## Prerequisites

- Java 24 or later
- Maven 3.6.3 or later
- Your favorite IDE (IntelliJ IDEA, Eclipse, VS Code, etc.)

## Getting Started

### Clone the Repository

```bash
git clone <repository-url>
cd Spring-security
```

### Build the Project

```bash
./mvnw clean install
```

### Run the Application

```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Authentication

- `POST /api/auth/login` - Authenticate and get JWT token
- `POST /api/auth/register` - Register a new user (if implemented)

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
