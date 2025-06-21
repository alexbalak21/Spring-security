package app.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration class that sets up Spring Security for the application.
 * Configures authentication, authorization, and security filters.
 */
@Configuration
@EnableWebSecurity // Enables Spring Security's web security support
public class SecurityConfig {

    /**
     * Configures the security filter chain that defines the security constraints
     * for different HTTP requests.
     *
     * @param http the HttpSecurity to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Disable CSRF as we're using JWT for authentication
                .csrf(AbstractHttpConfigurer::disable)
                
                // Configure URL authorization
                .authorizeHttpRequests(requests ->
                        requests
                                // Allow unauthenticated access to login endpoint
                                .requestMatchers("/api/login").permitAll()
                                // Require authentication for all other requests
                                .anyRequest().authenticated()
                )
                // Configure stateless session management (for JWT)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Disable basic authentication
                .httpBasic(AbstractHttpConfigurer::disable)
                // Disable form login as we're using JWT
                .formLogin(AbstractHttpConfigurer::disable)
                .build();
    }

    /**
     * Creates and configures the AuthenticationManager bean.
     *
     * @param authConfig the AuthenticationConfiguration to get the AuthenticationManager from
     * @return the configured AuthenticationManager
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Configures an in-memory user details service with a default admin user.
     * In a production environment, this should be replaced with a database-backed implementation.
     *
     * @return InMemoryUserDetailsManager with configured users
     */
    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        // Create a default admin user (for demonstration purposes only)
        UserDetails user = User.builder()
                .username("alex")  // Default username
                .password(passwordEncoder().encode("password"))  // Encoded password
                .roles("ADMIN")  // Granted role
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    /**
     * Creates a password encoder bean using BCrypt hashing.
     * BCrypt is currently the recommended password hashing algorithm.
     *
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}