package com.fedkoroma.security.service;

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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
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
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
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
        sendEmail(user, link, "Email Verification");
    }


    public String generateToken(String email){
        return jwtService.generateToken(email);
    }

    public void validateToken(String token){
        jwtService.validateToken(token);
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
