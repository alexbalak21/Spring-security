package app.spring.security;

import app.spring.entity.User;
import app.spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Custom implementation of Spring Security's UserDetailsService.
 * Loads user-specific data and performs authentication checks.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads a user by username and performs necessary account state checks.
     * This method is called during the authentication process.
     *
     * @param username the username identifying the user whose data is required
     * @return UserDetails containing the user's data and authorities
     * @throws UsernameNotFoundException if the user is not found
     * @throws DisabledException if the user account is disabled
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Find user by username (case-sensitive search)
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        
        // Check if account is active
        if (!user.isActive()) {
            throw new DisabledException("User account is disabled");
        }
        
        // Note: Account locking can be implemented by adding a 'locked' field to User entity
        // Example implementation for future use:
        // if (user.isLocked()) {
        //     throw new LockedException("User account is locked");
        // }
        
        // Check account expiration (example for future implementation)
        // if (user.isAccountExpired()) {
        //     throw new AccountExpiredException("User account has expired");
        // }
        
        // Check credentials expiration (example for future implementation)
        // if (user.areCredentialsExpired()) {
        //     throw new CredentialsExpiredException("User credentials have expired");
        // }
        
        return new UserDetailsImpl(user);
    }
}
