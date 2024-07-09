package com.fedkoroma.security.controller;

import com.fedkoroma.security.dto.AuthRequest;
import com.fedkoroma.security.model.User;
import com.fedkoroma.security.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class SecurityController {

    public final AuthService authService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public String addUser(@RequestBody User user){
        return authService.saveUser(user);
    }

    @PostMapping("/token")
    public String getToken(@RequestBody AuthRequest authRequest){
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
        if (authenticate.isAuthenticated()){
            return authService.generateToken(authRequest.getEmail());
        }else {
            throw new RuntimeException("invalid access");
        }
    }

    @GetMapping("/validate")
    public String validateToken(@RequestParam("token") String token){
        authService.validateToken(token);
        return "Token is valid";
    }


}
