package app.spring.service;

import app.spring.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {
    List<User> findAllUsers();
    Optional<User> findUserById(Long id);
    Optional<User> findUserByUsername(String username);
    User createUser(User user);
    User updateUser(User user);
    void deleteUser(Long id);
    boolean existsByUsername(String username);
    boolean existsById(Long id);
}
