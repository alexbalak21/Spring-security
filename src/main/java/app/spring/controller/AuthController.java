package app.spring.controller;

import app.spring.dto.LoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller handling authentication-related endpoints.
 * Provides login functionality and returns user details including roles.
 */
@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;

    /**
     * Constructor-based dependency injection.
     * @param authenticationManager The authentication manager to be injected
     */
    public AuthController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * Authenticates a user with the provided credentials and returns user details.
     *
     * @param loginRequest The login request containing username and password
     * @return ResponseEntity containing user details (username and roles) if authentication is successful
     */
    @PostMapping("/api/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        // 1. Create authentication token with provided credentials
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                );

        // 2. Authenticate the user using the authentication manager
        // This will throw an exception if authentication fails
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // 3. Set the authentication in the security context
        // This makes the authenticated user available for the current thread
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 4. Extract user roles from the authentication object
        // Convert Collection<GrantedAuthority> to List<String> of role names
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)  // Convert each GrantedAuthority to its string representation
                .collect(Collectors.toList());         // Collect results into a List

        // 5. Prepare the response with user details
        Map<String, Object> response = new HashMap<>();
        response.put("username", authentication.getName());  // Add username
        response.put("roles", roles);                       // Add user roles

        // 6. Return the response with HTTP 200 OK status
        return ResponseEntity.ok(response);
    }
}