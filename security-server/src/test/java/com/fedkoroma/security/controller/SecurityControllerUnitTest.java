package com.fedkoroma.security.controller;

import com.fedkoroma.security.dto.MessageResponse;
import com.fedkoroma.security.model.Role;
import com.fedkoroma.security.model.User;
import com.fedkoroma.security.repository.UserRepository;
import com.fedkoroma.security.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SecurityControllerUnitTest {

    @InjectMocks
    private SecurityController securityController;

    @Mock
    private AuthService authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddUser_Success() {
        // Arrange
        User user = new User("test@example.com","TestName", "TestName","test_password", LocalDateTime.now(), Role.USER);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = securityController.addUser(user);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User saved in system", ((MessageResponse) response.getBody()).getMessage());
        verify(authService, times(1)).saveUser(user);
    }

    @Test
    public void testAddUser_Failure_EmailExists() {
        // Arrange
        User user = new User("test@example.com","TestName", "TestName","test_password", LocalDateTime.now(), Role.USER);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // Act
        ResponseEntity<?> response = securityController.addUser(user);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User with this email already exists", ((MessageResponse) response.getBody()).getMessage());
        verify(authService, never()).saveUser(any());
    }

}