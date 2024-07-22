package com.fedkoroma.security.controller;

import com.fedkoroma.security.dto.AuthRequest;
import com.fedkoroma.security.dto.MessageResponse;
import com.fedkoroma.security.model.User;
import com.fedkoroma.security.repository.UserRepository;
import com.fedkoroma.security.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
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
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));

            if (authenticate.isAuthenticated()) {
                String token = authService.generateToken(authRequest.getEmail());
                return ResponseEntity.ok(token);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Invalid access"));
            }
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Invalid email or password"));
        }
    }

    @GetMapping("/validate")
    public  ResponseEntity<String> validateToken(@RequestParam("token") String token){
        authService.validateToken(token);
        return ResponseEntity.ok("Token is valid");
    }
}
