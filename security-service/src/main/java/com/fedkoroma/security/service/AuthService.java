package com.fedkoroma.security.service;

import com.fedkoroma.security.dto.UserDTO;
import com.fedkoroma.security.dto.UserRegistrationDTO;
import com.fedkoroma.security.email.EmailDetailDTO;
import com.fedkoroma.security.model.ConfirmationToken;
import com.fedkoroma.security.model.Role;
import com.fedkoroma.security.model.User;
import com.fedkoroma.security.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ConfirmationTokenService confirmationTokenService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.email.name}")
    private String emailExchange;

    @Value("${rabbitmq.binding.email.name}")
    private String emailRoutingKey;

    public void saveUser(@Valid UserRegistrationDTO userDto) {
        User user = createUser(userDto);
        userRepository.save(user);

        ConfirmationToken confirmationToken = createConfirmationToken(user);
        confirmationTokenService.saveConfirmationToken(confirmationToken);

        String verificationLink = createVerificationLink(confirmationToken.getToken());
        sendEmail(user, verificationLink, "Email Verification");

        // Отправка события о создании пользователя в RabbitMQ
        UserDTO userDTO = UserDTO.fromUser(user);
        rabbitTemplate.convertAndSend("user.exchange", "user.created", userDTO);
    }

    private User createUser(UserRegistrationDTO userDto) {
        return User.builder()
                .email(userDto.getEmail())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .registeredAt(LocalDateTime.now())
                .role(Role.USER)
                .locked(false)
                .enabled(false)
                .build();
    }

    private ConfirmationToken createConfirmationToken(User user) {
        String token = UUID.randomUUID().toString();
        return new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(60),
                user
        );
    }

    private String createVerificationLink(String token) {
        return "http://localhost:8765/auth/confirm?token=" + token;
    }

    public String generateToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return jwtService.generateToken(user.getEmail(), user.getRole());  // Передаем email и роль
    }

    private void sendEmail(User user, String token, String subject) {
        Map<String, Object> mailData = Map.of("token", token, "firstName", user.getFirstName());

        rabbitTemplate.convertAndSend(emailExchange, emailRoutingKey, EmailDetailDTO.builder()
                .to(user.getEmail())
                .subject(subject)
                .dynamicValue(mailData)
                .templateName("email_template")
                .build());
    }

    @Transactional
    public ResponseEntity<String> confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() -> new IllegalStateException("token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("email already confirmed");
        }

        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        userRepository.enableUser(confirmationToken.getUser().getEmail());

        return ResponseEntity.ok("confirmed");
    }
}
