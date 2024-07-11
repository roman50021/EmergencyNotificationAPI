package com.fedkoroma.security.model;

import com.fedkoroma.security.model.contract.AbstractEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_users")
public class User extends AbstractEntity implements UserDetails{

    @Column(name = "email", nullable = false)
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    @Column(name = "first_name", nullable = false)
    @NotBlank(message = "First Name is mandatory")
    @Size(max = 50, message = "FirstName should not exceed 50 characters")
    private String  firstName;

    @Column(name = "last_name", nullable = false)
    @NotBlank(message = "FirstName is mandatory")
    @Size(max = 50, message = "Last Name should not exceed 50 characters")
    private String  lastName;

    @Column(name = "password", nullable = false)
    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password should have at least 8 characters")
    private String password;

    @Column(name = "registered_at", nullable = false)
    private LocalDateTime registeredAt;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public UUID getId() {
        return super.getId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null; //List.of();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }
}
