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

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        
        // Check if account is enabled
        if (!user.isActive()) {
            throw new DisabledException("User account is disabled");
        }
        
        // Note: If you want to implement account locking in the future,
        // you can add a 'locked' boolean field to the User entity and uncomment this:
        // if (user.isLocked()) {
        //     throw new LockedException("User account is locked");
        // }
        
        return new UserDetailsImpl(user);
    }
}
