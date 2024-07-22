package com.fedkoroma.security.controller;

import com.fedkoroma.security.model.Role;
import com.fedkoroma.security.model.User;
import com.fedkoroma.security.repository.UserRepository;
import com.fedkoroma.security.service.AuthService;
import com.fedkoroma.security.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SecurityControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    private User testUser;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setRegisteredAt(LocalDateTime.now());
        testUser.setRole(Role.USER);
        userRepository.save(testUser);
    }

    @Test
    public void testRegisterUser_Success() throws Exception {
        String newUserJson = """
                {
                    "email": "newuser@example.com",
                    "firstName": "Jane",
                    "lastName": "Doe",
                    "password": "password"
                }
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User saved in system"));
    }

    @Test
    public void testRegisterUser_UserAlreadyExists() throws Exception {
        String existingUserJson = """
                {
                    "email": "test@example.com",
                    "firstName": "John",
                    "lastName": "Doe",
                    "password": "password"
                }
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(existingUserJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User with this email already exists"));
    }

    @Test
    public void testGetToken_Success() throws Exception {
        String authRequestJson = """
                {
                    "email": "test@example.com",
                    "password": "password"
                }
                """;

        mockMvc.perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isString());
    }

    @Test
    public void testGetToken_InvalidCredentials() throws Exception {
        String authRequestJson = """
                {
                    "email": "test@example.com",
                    "password": "wrongpassword"
                }
                """;

        mockMvc.perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authRequestJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    public void testValidateToken_Success() throws Exception {
        String token = jwtService.generateToken(testUser.getEmail());

        mockMvc.perform(get("/auth/validate")
                        .param("token", token))
                .andExpect(status().isOk())
                .andExpect(content().string("Token is valid"));
    }

    @Test
    public void testValidateToken_InvalidToken() throws Exception {
        String invalidToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmZWRrb3JvbWEyQGdtYWkuY29tIiwiaWF0IjoxNzIxMDYyNTk5LCJleHAiOjE3MjEwNjQzOTl8.RlDZIOcqoudeNj18tEog55rSOS2yOWV7xgwQ-TS3tVk";

        mockMvc.perform(get("/auth/validate")
                        .param("token", invalidToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid JWT signature"));
    }

    @Test
    public void testRegisterUserWithEmailValidError() throws Exception {
        String newUserJson = """
                {
                    "email": "newuserexample.com",
                    "firstName": "Jane",
                    "lastName": "Doe",
                    "password": "password"
                }
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email should be valid"));
    }

    @Test
    public void testRegisterUserWithPasswordValidError() throws Exception {
        String newUserJson = """
                {
                    "email": "newuser@example.com",
                    "firstName": "Jane",
                    "lastName": "Doe",
                    "password": "213"
                }
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Password should have at least 8 characters"));
    }
}
