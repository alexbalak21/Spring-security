# simple-app-sec-except.md

## 🔐 Simple Spring Boot App with Custom Security Exception Handling

This guide walks through a fun and functional Spring Boot project using Spring Security with custom exception handling.

---

## 📁 Project Structure

```
src/
 └── main/
     └── java/
         └── com/example/demo/
             ├── DemoApplication.java
             ├── config/SecurityConfig.java
             ├── security/CustomAuthenticationEntryPoint.java
             ├── security/CustomAccessDeniedHandler.java
             ├── controller/SecureController.java
             └── exception/UnauthorizedAccessException.java
```

---

## 💡 Features

- Basic authentication (`httpBasic`)
- Custom `401 Unauthorized` and `403 Forbidden` JSON responses
- Responses with flair (e.g. `"Not Allowed Here 🚫"`, `"You Shall Not Pass! 🧙‍♂️"`)

---

## 🧩 Step 1: Custom Authentication Entry Point

```java
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"401\", \"message\": \"Not Allowed Here 🚫\"}");
    }
}
```

---

## 🧱 Step 2: Custom Access Denied Handler

```java
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"403\", \"message\": \"You Shall Not Pass! 🧙‍♂️\"}");
    }
}
```

---

## ⚙️ Step 3: Security Configuration

```java
@Configuration
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint entryPoint;
    private final CustomAccessDeniedHandler deniedHandler;

    public SecurityConfig(CustomAuthenticationEntryPoint entryPoint, CustomAccessDeniedHandler deniedHandler) {
        this.entryPoint = entryPoint;
        this.deniedHandler = deniedHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/secret").authenticated()
                .anyRequest().permitAll()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(entryPoint)
                .accessDeniedHandler(deniedHandler)
            )
            .httpBasic();

        return http.build();
    }
}
```

---

## 📦 Step 4: Simple Secured Controller

```java
@RestController
@RequestMapping("/api")
public class SecureController {

    @GetMapping("/secret")
    public String getSecret() {
        return "🎁 This is classified. Good job being authenticated!";
    }
}
```

---

## 🧪 Testing It Out

- Access `/api/secret` without credentials → `401 Not Allowed Here 🚫`
- Access with insufficient privileges (if configured) → `403 You Shall Not Pass! 🧙‍♂️`
- Authenticate → Receive secure content 🎉

---

Want to make it even sillier with animated emojis or themed HTTP responses like `"Area 51 Access Denied"`? Just say the word.
