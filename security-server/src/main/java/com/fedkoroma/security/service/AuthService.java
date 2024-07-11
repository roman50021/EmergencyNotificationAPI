package com.fedkoroma.security.service;

import com.fedkoroma.security.model.Role;
import com.fedkoroma.security.model.User;
import com.fedkoroma.security.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String saveUser(@Valid User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if(existingUser.isPresent()){
            return "User with this email already exists";
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRegisteredAt(LocalDateTime.now());
            user.setRole(Role.USER);
            userRepository.save(user);
            return "User saved in system";
        }
    }

    public String generateToken(String email){
        return jwtService.generateToken(email);
    }

    public void validateToken(String token){
        jwtService.validateToken(token);
    }
}
