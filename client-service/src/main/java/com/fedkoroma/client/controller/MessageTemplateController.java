package com.fedkoroma.client.controller;

import com.fedkoroma.client.dto.MessageTemplateDTO;
import com.fedkoroma.client.service.AuthService;
import com.fedkoroma.client.service.MessageTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/templates")
@RequiredArgsConstructor
public class MessageTemplateController {

    private final MessageTemplateService messageTemplateService;
    private final AuthService authService;


    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid MessageTemplateDTO messageTemplateDTO,
                                    @RequestHeader("Authorization") String token) {
        String email = authService.getEmailFromToken(token);
        MessageTemplateDTO createdTemplate = messageTemplateService.createMessageTemplate(messageTemplateDTO, email);
        return new ResponseEntity<>(createdTemplate, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MessageTemplateDTO>> all(@RequestHeader("Authorization") String token) {
        String email =  authService.getEmailFromToken(token);
        List<MessageTemplateDTO> templates = messageTemplateService.getAllTemplatesByUser(email);
        return new ResponseEntity<>(templates, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageTemplateDTO> getById(@PathVariable Long id,
                                              @RequestHeader("Authorization") String token) {
        String email = authService.getEmailFromToken(token);
        MessageTemplateDTO messageTemplateDTO = messageTemplateService.getTemplateByIdAndUser(id, email);
        return new ResponseEntity<>(messageTemplateDTO, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageTemplateDTO> update(@PathVariable Long id,
                                             @RequestBody @Valid MessageTemplateDTO messageTemplateDTO,
                                             @RequestHeader("Authorization") String token) {
        String email = authService.getEmailFromToken(token);
        MessageTemplateDTO updatedTemplate = messageTemplateService.updateTemplate(id, messageTemplateDTO, email);
        return new ResponseEntity<>(updatedTemplate, HttpStatus.OK);
    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @RequestHeader("Authorization") String token) {
        String email = authService.getEmailFromToken(token);
        messageTemplateService.deleteTemplate(id, email);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
