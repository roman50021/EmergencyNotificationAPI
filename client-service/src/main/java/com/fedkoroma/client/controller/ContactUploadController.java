package com.fedkoroma.client.controller;

import com.fedkoroma.client.service.AuthService;
import com.fedkoroma.client.service.ContactUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v2/contacts")
@RequiredArgsConstructor
public class ContactUploadController {

    private final ContactUploadService contactUploadService;
    private final AuthService authService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadContactsFile(@RequestParam("file") MultipartFile file,
                                                     @RequestHeader("Authorization") String token) {
        String email = authService.getEmailFromToken(token);
        try {
            contactUploadService.processFile(file, email);
            return ResponseEntity.status(HttpStatus.OK).body("File uploaded and processed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to upload and process file.");
        }
    }
}
