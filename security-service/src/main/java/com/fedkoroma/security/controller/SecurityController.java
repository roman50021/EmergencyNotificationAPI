package com.fedkoroma.security.controller;

import com.fedkoroma.security.dto.AuthRequest;
import com.fedkoroma.security.dto.AuthResponse;
import com.fedkoroma.security.dto.MessageResponse;
import com.fedkoroma.security.dto.UserRegistrationDTO;
import com.fedkoroma.security.model.Role;
import com.fedkoroma.security.model.User;
import com.fedkoroma.security.repository.UserRepository;
import com.fedkoroma.security.service.AuthService;
import com.fedkoroma.security.service.JwtService;
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
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> addUser(@Valid @RequestBody UserRegistrationDTO userDto) {
        Optional<User> existingUser = userRepository.findByEmail(userDto.getEmail());
        if(existingUser.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("User with this email already exists"));
        } else {
            authService.saveUser(userDto);
            return ResponseEntity.ok(new MessageResponse("User saved in system"));
        }
    }

    @PostMapping("/token")
    public ResponseEntity<?> getToken(@Valid @RequestBody AuthRequest authRequest) {
        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));

            UserDetails userDetails = (UserDetails) authenticate.getPrincipal();
            User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                    () -> new DisabledException("User not found")
            );

            if (!user.isEnabled()) {
                throw new DisabledException("Confirm your account by email");
            }

            if (!user.isAccountNonLocked()) {
                throw new LockedException("Your account has been blocked");
            }

            String token = authService.generateToken(authRequest.getEmail());
            return ResponseEntity.ok(token);
        } catch (DisabledException | LockedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Invalid email or password"));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<AuthResponse> validateToken(@RequestParam("token") String token) {
        String email = jwtService.extractUsername(token);
        Boolean valid = jwtService.extractValid(token);
        Role role = jwtService.extractRole(token);
        jwtService.validateToken(token); // Проверяем токен
        AuthResponse response = new AuthResponse(email, valid, role); // Пример заполнения
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "confirm")
    public ResponseEntity<String> confirm(@RequestParam("token") String token) {
        return authService.confirmToken(token);
    }
}
