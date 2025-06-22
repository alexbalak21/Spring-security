# SEC-EXCEPT-IMPL.md

## 🔐 Spring Security Exception Handling Implementation Guide

This document outlines the custom exception handling approach used in the application for managing authentication and authorization errors, using both Spring Security filters and controller-level exception handling.

---

## 📌 Goal

To provide meaningful and consistent JSON responses for:
- `401 Unauthorized`: Missing or invalid credentials.
- `403 Forbidden`: Insufficient access permissions.
- Application-specific security-related exceptions.

---

## 1. Custom Exceptions

```java
public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}

public class ForbiddenAccessException extends RuntimeException {
    public ForbiddenAccessException(String message) {
        super(message);
    }
}
```

---

## 2. Global Exception Handler (`@ControllerAdvice`)

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorized(UnauthorizedAccessException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            Map.of("error", "Unauthorized", "message", ex.getMessage())
        );
    }

    @ExceptionHandler(ForbiddenAccessException.class)
    public ResponseEntity<Map<String, String>> handleForbidden(ForbiddenAccessException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            Map.of("error", "Forbidden", "message", ex.getMessage())
        );
    }
}
```

---

## 3. Security Layer – Custom Handlers

### `AuthenticationEntryPoint` for 401

```java
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"" + authException.getMessage() + "\"}");
    }
}
```

### `AccessDeniedHandler` for 403

```java
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Forbidden\", \"message\": \"" + accessDeniedException.getMessage() + "\"}");
    }
}
```

---

## 4. Spring Security Configuration

```java
@Configuration
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint authEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(CustomAuthenticationEntryPoint authEntryPoint,
                          CustomAccessDeniedHandler accessDeniedHandler) {
        this.authEntryPoint = authEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            )
            .httpBasic(); // or jwt, formLogin, etc.

        return http.build();
    }
}
```

---

## 🧪 Testing Scenarios

- Access `/api/secure-data` without credentials → returns JSON `401 Unauthorized`.
- Access with valid credentials but insufficient role → JSON `403 Forbidden`.
- Simulate application

---

## 🧪 Controller Example Using GlobalExceptionHandler

Here's a sample controller that demonstrates how application-level exceptions (like `UnauthorizedAccessException`) are thrown and handled by the `GlobalExceptionHandler`.

### 📦 Sample Controller

```java
@RestController
@RequestMapping("/api")
public class SecureDataController {

    @GetMapping("/secure-data")
    public ResponseEntity<String> getSecureData() {
        boolean isAuthenticated = false; // Simulate auth check

        if (!isAuthenticated) {
            throw new UnauthorizedAccessException("You must be logged in to access this resource.");
        }

        return ResponseEntity.ok("This is classified data.");
    }

    @GetMapping("/admin-data")
    public ResponseEntity<String> getAdminData() {
        boolean hasAdminRights = false; // Simulate role check

        if (!hasAdminRights) {
            throw new ForbiddenAccessException("You do not have permission to access admin data.");
        }

        return ResponseEntity.ok("Welcome, admin user!");
    }
}
```

### 🔁 Expected Responses

- Access `/api/secure-data` without valid auth → triggers `UnauthorizedAccessException` → handled by `GlobalExceptionHandler`.
- Access `/api/admin-data` without proper role → triggers `ForbiddenAccessException` → handled similarly.

---


---

## 🎯 Simple Example Using @ControllerAdvice-based GlobalExceptionHandler

Great! Here's a simple example of a Spring Boot controller that uses a `@ControllerAdvice`-based `GlobalExceptionHandler` to handle a custom `UnauthorizedAccessException`.

### 🧩 Step 1: Define a Custom Exception

```java
public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
```

### 🎯 Step 2: Create the REST Controller

```java
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DemoController {

    @GetMapping("/secure-data")
    public String getSecureData() {
        // Simulate authentication check
        boolean isAuthenticated = false;

        if (!isAuthenticated) {
            throw new UnauthorizedAccessException("You must be logged in to access this resource.");
        }

        return "Top secret data!";
    }
}
```

### 🚨 Step 3: Implement Global Exception Handler

```java
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorized(UnauthorizedAccessException ex) {
        Map<String, String> body = Map.of(
            "error", "Unauthorized",
            "message", ex.getMessage()
        );
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }
}
```

📦 Put it all together, and now your controller throws a custom exception, which is neatly caught and formatted by your global handler.

---


