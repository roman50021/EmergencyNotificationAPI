package com.fedkoroma.security.service;

import com.fedkoroma.security.dto.MessageResponse;
import com.fedkoroma.security.email.EmailSender;
import com.fedkoroma.security.model.ConfirmationToken;
import com.fedkoroma.security.model.Role;
import com.fedkoroma.security.model.User;
import com.fedkoroma.security.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSender emailSender;

    public void saveUser(@Valid User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRegisteredAt(LocalDateTime.now());
        user.setRole(Role.USER);
        userRepository.save(user);

        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user
        );
        confirmationTokenService.saveConfirmationToken(confirmationToken);

        String link = "http://localhost:8765/auth/confirm?token=" + token;
        emailSender.send(user.getEmail(), user.getFirstName(), link);
    }

    public String generateToken(String email){
        return jwtService.generateToken(email);
    }

    public void validateToken(String token){
        jwtService.validateToken(token);
    }

    @Transactional
    public ResponseEntity<String> confirmToken(String token){
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() -> new IllegalStateException("token not found"));

        if(confirmationToken.getConfirmedAt() != null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("email already confirmed");
        }

        LocalDateTime expiredAt =  confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        userRepository.enableUser(confirmationToken.getUser().getEmail());

        return ResponseEntity.ok("confirmed");
    }

}
