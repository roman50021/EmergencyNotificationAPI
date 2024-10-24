package com.fedkoroma.client.controller;

import com.fedkoroma.client.dto.MessageDTO;
import com.fedkoroma.client.dto.MessageTemplateDTO;
import com.fedkoroma.client.service.AuthService;
import com.fedkoroma.client.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<MessageDTO> create(@RequestBody @Valid MessageDTO messageDTO,
                                             @RequestHeader("Authorization") String token) {
        String email = authService.getEmailFromToken(token);
        MessageDTO message = messageService.createMessage(messageDTO, email);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MessageDTO>> all(@RequestHeader("Authorization") String token) {
        String email =  authService.getEmailFromToken(token);
        List<MessageDTO> messages = messageService.getAllMessagesByUser(email);
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @PostMapping("/send/{id}")
    public ResponseEntity<String> send(@PathVariable Long id,
                                              @RequestHeader("Authorization") String token) {
        String email = authService.getEmailFromToken(token);
        messageService.sendMessage(id, email);
        return new ResponseEntity<>("Message sent successfully!", HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageDTO> update(@PathVariable Long id,
                                                     @RequestBody @Valid MessageDTO messageDTO,
                                                     @RequestHeader("Authorization") String token) {
        String email = authService.getEmailFromToken(token);
        MessageDTO updatedMessage = messageService.updateMessage(id, messageDTO, email);
        return new ResponseEntity<>(updatedMessage, HttpStatus.OK);
    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @RequestHeader("Authorization") String token) {
        String email = authService.getEmailFromToken(token);
        messageService.deleteMessage(id, email);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
