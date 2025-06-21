package app.spring.dto;

import java.util.Set;

public class UserDTO {
    private Long id;
    private String username;
    private Set<String> roles;
    private boolean active;

    // Constructors
    public UserDTO() {}

    public UserDTO(Long id, String username, Set<String> roles, boolean active) {
        this.id = id;
        this.username = username;
        this.roles = roles;
        this.active = active;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // Utility method to convert from Entity to DTO
    public static UserDTO fromEntity(app.spring.entity.User user) {
        return new UserDTO(
            user.getId(),
            user.getUsername(),
            user.getRoles(),
            user.isActive()
        );
    }
}
