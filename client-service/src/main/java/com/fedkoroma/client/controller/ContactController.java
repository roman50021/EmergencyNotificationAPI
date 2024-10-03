package com.fedkoroma.client.controller;

import com.fedkoroma.client.dto.ContactDTO;
import com.fedkoroma.client.service.AuthService;
import com.fedkoroma.client.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<ContactDTO>> all(@RequestHeader("Authorization") String token) {
        String email =  authService.getEmailFromToken(token);
        List<ContactDTO> contacts = contactService.getAllContactsByUser(email);
        return new ResponseEntity<>(contacts, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactDTO> getById(@PathVariable Long id,
                                              @RequestHeader("Authorization") String token) {
        String email = authService.getEmailFromToken(token);
        ContactDTO contactDTO = contactService.getContactByIdAndUser(id, email);
        return new ResponseEntity<>(contactDTO, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactDTO> update(@PathVariable Long id,
                                    @RequestBody @Valid ContactDTO contactDTO,
                                    @RequestHeader("Authorization") String token) {
        String email = authService.getEmailFromToken(token);
        ContactDTO updatedContact = contactService.updateContact(id, contactDTO, email);
        return new ResponseEntity<>(updatedContact, HttpStatus.OK);
    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @RequestHeader("Authorization") String token) {
        String email = authService.getEmailFromToken(token);
        contactService.deleteContact(id, email);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
