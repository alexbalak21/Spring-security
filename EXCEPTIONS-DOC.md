# Exception Handling Documentation

## Table of Contents
1. [Overview](#overview)
2. [How It Works](#how-it-works)
3. [Exception Handling Flow](#exception-handling-flow)
4. [Why It Overrides SecurityConfig Exceptions](#why-it-overrides-securityconfig-exceptions)
5. [Response Format](#response-format)
6. [Authentication Exceptions](#authentication-exceptions)
7. [Implementation Details](#implementation-details)
8. [Customizing Exception Handling](#customizing-exception-handling)

## Overview
The application uses a centralized exception handling mechanism through the `GlobalExceptionHandler` class, which provides consistent JSON error responses for various types of exceptions, including those from Spring Security.

## How It Works

The exception handling is implemented using Spring's `@ControllerAdvice` mechanism with these key components:

1. **`@RestControllerAdvice`**
   - A specialized version of `@ControllerAdvice` that includes `@ResponseBody`
   - Makes the class a global exception handler for all controllers
   - Processes exceptions before they reach the client

2. **`ResponseEntityExceptionHandler`**
   - Base class that provides handling for standard Spring MVC exceptions
   - Can be extended to customize the response for specific exceptions

3. **`@ExceptionHandler`**
   - Methods annotated with `@ExceptionHandler` define how to handle specific exceptions
   - Can return `ResponseEntity` to fully control the response

## Exception Handling Flow

1. When an exception is thrown during request processing:
   - Spring's `DispatcherServlet` catches the exception
   - It looks for the closest exception handler in the call stack
   - If no local handler is found, it delegates to the `@ControllerAdvice` handlers

2. For authentication/authorization exceptions:
   - Spring Security throws exceptions (like `BadCredentialsException`)
   - These exceptions propagate through the security filter chain
   - Our `GlobalExceptionHandler` catches them and converts them to standardized JSON responses

## Why It Overrides SecurityConfig Exceptions

The `GlobalExceptionHandler` takes precedence over `SecurityConfig` exception handling because:

1. **Order of Processing**:
   - `@ControllerAdvice`/`@RestControllerAdvice` handlers are processed after the security filter chain but before the default Spring error handling
   - They have higher precedence than Spring Security's default error pages

2. **Global Scope**:
   - `@RestControllerAdvice` is global to the entire application
   - It catches exceptions before they reach the security configuration's error handling

3. **Exception Resolution Order**:
   - First, Spring looks for a matching `@ExceptionHandler` in the current controller
   - Then, it checks `@ControllerAdvice` classes (like our `GlobalExceptionHandler`)
   - Finally, it falls back to the default error handling

4. **Benefits of This Approach**:
   - Centralized exception handling
   - Consistent error responses across the entire application
   - Separation of concerns (security configuration vs. error handling)
   - Easier maintenance and updates to error handling logic

## Response Format
All error responses follow this structure:

```json
{
  "timestamp": "2025-06-22T02:53:52.3909609",
  "status": 401,
  "error": "Unauthorized",
  "code": "ERROR_CODE",
  "message": "Human-readable error message"
}
```

## Authentication Exceptions

### 401 Unauthorized
- **Code:** `INVALID_CREDENTIALS`
- **Triggered when:** Invalid username or password is provided
- **Example Response:**
  ```json
  {
    "timestamp": "2025-06-22T02:53:52.3909609",
    "status": 401,
    "error": "Unauthorized",
    "code": "INVALID_CREDENTIALS",
    "message": "Invalid username or password"
  }
  ```

### 403 Forbidden
- **Code:** `ACCOUNT_DISABLED`
- **Triggered when:** User account is disabled
- **Example Response:**
  ```json
  {
    "status": 403,
    "error": "Forbidden",
    "code": "ACCOUNT_DISABLED",
    "message": "User account is disabled"
  }
  ```

### 403 Forbidden
- **Code:** `ACCOUNT_EXPIRED`
- **Triggered when:** User account has expired
- **Example Response:**
  ```json
  {
    "status": 403,
    "error": "Forbidden",
    "code": "ACCOUNT_EXPIRED",
    "message": "User account has expired"
  }
  ```

### 403 Forbidden
- **Code:** `CREDENTIALS_EXPIRED`
- **Triggered when:** User credentials have expired
- **Example Response:**
  ```json
  {
    "status": 403,
    "error": "Forbidden",
    "code": "CREDENTIALS_EXPIRED",
    "message": "User credentials have expired"
  }
  ```

### 423 Locked
- **Code:** `ACCOUNT_LOCKED`
- **Triggered when:** User account is locked
- **Example Response:**
  ```json
  {
    "status": 423,
    "error": "Locked",
    "code": "ACCOUNT_LOCKED",
    "message": "User account is locked"
  }
  ```

## Validation Exceptions (400 Bad Request)
- **Code:** `VALIDATION_ERROR`
- **Triggered when:** Request validation fails (e.g., invalid input format)
- **Example Response:**
  ```json
  {
    "timestamp": "2025-06-22T03:15:22.123456",
    "status": 400,
    "error": "Bad Request",
    "code": "VALIDATION_ERROR",
    "message": "Validation failed",
    "errors": {
      "email": "must be a well-formed email address",
      "password": "size must be between 8 and 100"
    }
  }
  ```

## Internal Server Error (500)
- **Code:** `INTERNAL_SERVER_ERROR`
- **Triggered when:** An unexpected error occurs
- **Example Response:**
  ```json
  {
    "timestamp": "2025-06-22T03:20:45.789012",
    "status": 500,
    "error": "Internal Server Error",
    "code": "INTERNAL_SERVER_ERROR",
    "message": "An unexpected error occurred: [exception message]"
  }
  ```

## Implementation Details

### GlobalExceptionHandler Class
```java
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    // Exception handlers for different exception types
}
```

### Key Components
1. **`@ExceptionHandler` Methods**
   - Each method handles a specific exception type
   - Returns a `ResponseEntity` with the appropriate HTTP status and error details

2. **`buildErrorResponse` Method**
   - Centralized method to create consistent error responses
   - Ensures all error responses follow the same structure

## Customizing Exception Handling

### To Add a New Exception Handler
1. Add a new method in `GlobalExceptionHandler`:
   ```java
   @ExceptionHandler(YourCustomException.class)
   public ResponseEntity<Object> handleYourCustomException(YourCustomException ex) {
       return buildErrorResponse(
           HttpStatus.BAD_REQUEST,
           "YOUR_ERROR_CODE",
           ex.getMessage()
       );
   }
   ```

### To Modify Existing Behavior
1. Locate the appropriate `@ExceptionHandler` method in `GlobalExceptionHandler`
2. Update the logic or response format as needed

### To Disable Global Exception Handling
1. Remove the `@RestControllerAdvice` annotation from `GlobalExceptionHandler`
2. Handle exceptions in individual controllers or use Spring Security's error handling

## Best Practices
1. Keep exception handling logic simple and focused
2. Log all exceptions for debugging purposes
3. Don't expose sensitive information in error responses
4. Use meaningful error codes and messages
5. Document all possible error responses in your API documentation
