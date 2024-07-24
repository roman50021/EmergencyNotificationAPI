package com.fedkoroma.security.controller;

import com.fedkoroma.security.dto.AuthRequest;
import com.fedkoroma.security.dto.MessageResponse;
import com.fedkoroma.security.exception.AccountLockedException;
import com.fedkoroma.security.exception.AccountNotConfirmedException;
import com.fedkoroma.security.model.User;
import com.fedkoroma.security.repository.UserRepository;
import com.fedkoroma.security.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class SecurityController {

    public final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> addUser(@Valid @RequestBody User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if(existingUser.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("User with this email already exists"));
        } else {
            authService.saveUser(user);
            return ResponseEntity.ok(new MessageResponse("User saved in system"));
        }
    }

    @PostMapping("/token")
    public ResponseEntity<?> getToken(@Valid @RequestBody AuthRequest authRequest) {
        try {
            // Пытаемся аутентифицировать пользователя
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));

            // Проверяем, прошла ли аутентификация
            if (authenticate.isAuthenticated()) {
                UserDetails userDetails = (UserDetails) authenticate.getPrincipal();
                // Ищем пользователя в репозитории
                User user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);

                // Если пользователь не найден, возвращаем ошибку
                if (user == null) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("User not found"));
                }

                // Проверяем состояние аккаунта пользователя
                if (!user.isEnabled()) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Confirm your account by email"));
                }

                if (!user.isAccountNonLocked()) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Your account has been blocked"));
                }

                // Генерируем токен (или другую нужную информацию)
                String token = authService.generateToken(authRequest.getEmail());
                return ResponseEntity.ok(token);
            }

            // Если аутентификация не прошла, возвращаем ошибку
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Invalid email or password"));

        } catch (DisabledException e) {
            // Обработка случая, когда аккаунт отключен
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Your account is disabled"));
        } catch (LockedException e) {
            // Обработка случая, когда аккаунт заблокирован
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Your account has been blocked"));
        } catch (AccountNotConfirmedException e) {
            // Обработка случая, когда аккаунт не подтвержден
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Confirm your account by email"));
        } catch (Exception e) {
            // Обработка других исключений
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("An unexpected error occurred"));
        }
    }

    @GetMapping("/validate")
    public  ResponseEntity<String> validateToken(@RequestParam("token") String token){
        authService.validateToken(token);
        return ResponseEntity.ok("Token is valid");
    }
}
