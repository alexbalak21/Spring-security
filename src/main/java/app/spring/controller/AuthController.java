package app.spring.controller;

import app.spring.dto.LoginRequest;
import app.spring.dto.RegisterRequest;
import app.spring.entity.User;

import app.spring.repository.UserRepository;
import app.spring.security.UserDetailsImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller handling authentication-related endpoints.
 * Provides login and registration functionality.
 */
/**
 * Controller handling authentication-related endpoints including user registration and login.
 * All endpoints are publicly accessible.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor for dependency injection.
     *
     * @param authenticationManager Handles the authentication process
     * @param userRepository Repository for user data access
     * @param passwordEncoder Encoder for password hashing
     */
    public AuthController(AuthenticationManager authenticationManager,
                         UserRepository userRepository,
                         PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user with the provided credentials.
     * Validates the request and ensures the username is unique.
     *
     * @param registerRequest The registration request containing username and password
     * @return ResponseEntity with the created user's details (excluding password)
     * @throws ResponseStatusException with CONFLICT status if username already exists
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        // Check if username is already taken
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            Map<String, Object> response = new HashMap<>();
            response.put("timestamp", java.time.LocalDateTime.now());
            response.put("status", HttpStatus.CONFLICT.value());
            response.put("error", HttpStatus.CONFLICT.getReasonPhrase());
            response.put("code", "USERNAME_ALREADY_EXISTS");
            response.put("message", "Username '" + registerRequest.getUsername() + "' is already taken");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        // Create new user
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRoles(Collections.singleton("ROLE_USER"));
        user.setActive(true);
        
        User savedUser = userRepository.save(user);

        // Prepare the response with user details (excluding sensitive data like password)
        Map<String, Object> response = new HashMap<>();
        response.put("id", savedUser.getId());
        response.put("username", savedUser.getUsername());
        response.put("active", savedUser.isActive());
        response.put("roles", savedUser.getRoles());
        response.put("createdAt", savedUser.getCreatedAt());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Authenticates a user with the provided credentials and returns user details.
     *
     * @param loginRequest The login request containing username and password
     * @return ResponseEntity containing user details (username and roles) if authentication is successful
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // 1. Create authentication token with provided credentials
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );

            // 2. Authenticate the user using the authentication manager
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // 3. Set the authentication in the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 4. Extract user details
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User user = userDetails.getUser();
            
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            // 5. Prepare the response with user details
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("username", userDetails.getUsername());
            response.put("roles", roles);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw e; // Let the GlobalExceptionHandler handle it
        }
    }
}