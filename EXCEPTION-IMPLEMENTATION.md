# Spring Security Global Exception Handling Implementation Guide

This guide provides a step-by-step implementation of global exception handling in a Spring Boot application with Spring Security.

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Implementation Steps](#implementation-steps)
3. [Testing the Implementation](#testing-the-implementation)
4. [Common Issues and Solutions](#common-issues-and-solutions)
5. [Best Practices](#best-practices)

## Prerequisites

- Java 11 or higher
- Spring Boot 2.7.x or higher
- Maven or Gradle
- Spring Security
- Spring Web
- Spring Validation (for request validation)

## Implementation Steps

### 1. Add Required Dependencies

Add these to your `pom.xml`:

```xml
<dependencies>
    <!-- Spring Boot Starter Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Spring Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- Lombok (Optional) -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

### 2. Create the Global Exception Handler

Create a new class `GlobalExceptionHandler` in your exception package:

```java
package com.yourproject.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // Handle invalid credentials
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Invalid username or password");
    }

    // Handle disabled account
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Object> handleDisabledException(DisabledException ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, "ACCOUNT_DISABLED", "User account is disabled");
    }

    // Handle account expired
    @ExceptionHandler(AccountExpiredException.class)
    public ResponseEntity<Object> handleAccountExpiredException(AccountExpiredException ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, "ACCOUNT_EXPIRED", "User account has expired");
    }

    // Handle credentials expired
    @ExceptionHandler(CredentialsExpiredException.class)
    public ResponseEntity<Object> handleCredentialsExpiredException(CredentialsExpiredException ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, "CREDENTIALS_EXPIRED", "User credentials have expired");
    }

    // Handle locked account
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<Object> handleLockedException(LockedException ex) {
        return buildErrorResponse(HttpStatus.LOCKED, "ACCOUNT_LOCKED", "User account is locked");
    }

    // Handle user not found
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", ex.getMessage());
    }

    // Handle validation errors
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, 
            HttpStatusCode status, WebRequest request) {
        
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", HttpStatus.valueOf(status.value()).getReasonPhrase());
        body.put("code", "VALIDATION_ERROR");
        
        // Get field errors
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                    fieldError -> fieldError.getField(),
                    fieldError -> fieldError.getDefaultMessage() != null ? 
                                 fieldError.getDefaultMessage() : "Validation error"
                ));
        
        body.put("errors", fieldErrors);
        
        // Create a summary message
        String errorMessage = fieldErrors.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));
        body.put("message", errorMessage);
        
        return new ResponseEntity<>(body, status);
    }

    // Catch-all for unhandled exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllUncaughtException(Exception ex) {
        return buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR, 
            "INTERNAL_SERVER_ERROR", 
            "An unexpected error occurred: " + ex.getMessage()
        );
    }

    // Helper method to build consistent error responses
    private ResponseEntity<Object> buildErrorResponse(HttpStatus status, String errorCode, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("code", errorCode);
        body.put("message", message);

        return new ResponseEntity<>(body, status);
    }
}
```

### 3. Configure Spring Security (Optional)

If you need to customize security configuration, create a security config class:

```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }
}
```

### 4. Create a Test Controller

Create a controller to test the exception handling:

```java
@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @GetMapping("/not-found")
    public void throwNotFound() {
        throw new UsernameNotFoundException("User not found with given email");
    }
    
    @PostMapping("/validate")
    public void testValidation(@Valid @RequestBody UserDTO user) {
        // Validation will be handled by @Valid and MethodArgumentNotValidException
    }
    
    @GetMapping("/error")
    public void throwError() {
        throw new RuntimeException("This is a test error");
    }
}
```

## Testing the Implementation

### 1. Test with Invalid Credentials
```http
POST /api/auth/login
Content-Type: application/json

{
    "username": "wronguser",
    "password": "wrongpass"
}
```
**Expected Response (401 Unauthorized):**
```json
{
    "timestamp": "2025-06-22T03:15:22.123456",
    "status": 401,
    "error": "Unauthorized",
    "code": "INVALID_CREDENTIALS",
    "message": "Invalid username or password"
}
```

### 2. Test Validation Errors
```http
POST /api/test/validate
Content-Type: application/json

{
    "email": "invalid-email",
    "password": "123"
}
```

**Expected Response (400 Bad Request):**
```json
{
    "timestamp": "2025-06-22T03:16:45.678901",
    "status": 400,
    "error": "Bad Request",
    "code": "VALIDATION_ERROR",
    "errors": {
        "email": "must be a well-formed email address",
        "password": "size must be between 8 and 100"
    },
    "message": "email: must be a well-formed email address, password: size must be between 8 and 100"
}
```

## Common Issues and Solutions

### 1. GlobalExceptionHandler Not Catching Exceptions
- **Cause**: The exception might be caught by Spring Security's filter chain before reaching your handler
- **Solution**: Ensure your exception extends `RuntimeException` and is not caught by any `try-catch` blocks before reaching the controller

### 2. Validation Errors Not Working
- **Cause**: Missing `@Valid` annotation or validation dependency
- **Solution**: 
  - Add `@Valid` before `@RequestBody`
  - Ensure `spring-boot-starter-validation` is in your dependencies

### 3. Custom Exceptions Not Being Handled
- **Cause**: Missing `@ExceptionHandler` method for your custom exception
- **Solution**: Add a new method in `GlobalExceptionHandler` with `@ExceptionHandler(YourCustomException.class)`

## Best Practices

1. **Log All Exceptions**
   - Add logging in your exception handlers for debugging
   - Use appropriate log levels (ERROR for server errors, WARN for client errors)

2. **Use Specific Exceptions**
   - Create custom exceptions for your business logic
   - Extend `RuntimeException` for unchecked exceptions

3. **Keep Error Messages Generic**
   - Don't expose sensitive information in error responses
   - Log detailed errors server-side instead

4. **Document Your API**
   - Document all possible error responses in your API documentation
   - Include error codes and their meanings

5. **Test Thoroughly**
   - Write tests for all exception scenarios
   - Test both happy paths and error cases

6. **Handle Security Exceptions**
   - Ensure security exceptions are properly handled and logged
   - Don't leak security information in error responses

7. **Use Consistent Error Format**
   - Maintain a consistent error response format across your API
   - Include timestamps, error codes, and helpful messages

8. **Handle Async Operations**
   - If using async operations, ensure exceptions are properly propagated
   - Consider using `@Async` with a custom `AsyncUncaughtExceptionHandler`

By following this guide, you'll have a robust exception handling system in place that works seamlessly with Spring Security and provides meaningful error responses to your API consumers.
