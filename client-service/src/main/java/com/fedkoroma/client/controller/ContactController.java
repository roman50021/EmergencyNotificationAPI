package com.fedkoroma.client.controller;

import com.fedkoroma.client.dto.ContactDTO;
import com.fedkoroma.client.service.AuthService;
import com.fedkoroma.client.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid ContactDTO contactDTO,
                                    @RequestHeader("Authorization") String token) {
        String email = authService.getEmailFromToken(token);
        ContactDTO createdContact = contactService.createContact(contactDTO, email);
        return new ResponseEntity<>(createdContact, HttpStatus.CREATED);
    }

    @GetMapping("/read")
    public ResponseEntity<?> read() {
        return null;
    }

    @GetMapping("/all")
    public ResponseEntity<?> all() {
        return null;
    }

    @PutMapping("/update")
    public ResponseEntity<?> update() {
        return null;
    }

    @DeleteMapping ("/delete")
    public ResponseEntity<?> delete() {
        return null;
    }
}
