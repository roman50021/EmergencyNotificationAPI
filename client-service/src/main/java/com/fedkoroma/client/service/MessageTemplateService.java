package com.fedkoroma.client.service;

import com.fedkoroma.client.dto.MessageTemplateDTO;
import com.fedkoroma.client.exception.EmailNotFoundException;
import com.fedkoroma.client.exception.ResourceNotFoundException;
import com.fedkoroma.client.model.MessageTemplate;
import com.fedkoroma.client.model.User;
import com.fedkoroma.client.repository.MessageTemplateRepository;
import com.fedkoroma.client.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageTemplateService {

    private final MessageTemplateRepository messageTemplateRepository;
    private final UserRepository userRepository;

    public MessageTemplateDTO createMessageTemplate(MessageTemplateDTO messageTemplateDTO, String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("User not found"));

        MessageTemplate template = new MessageTemplate();
        template.setTitle(messageTemplateDTO.getTitle());
        template.setBody(messageTemplateDTO.getBody());
        template.setUser(user);
        messageTemplateRepository.save(template);

        return mapToDTO(template);
    }

    public List<MessageTemplateDTO> getAllTemplatesByUser(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("User not found"));

        return messageTemplateRepository.findAllByUser(user)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public MessageTemplateDTO getTemplateByIdAndUser(Long id, String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("User not found"));

        MessageTemplate template = messageTemplateRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));

        return mapToDTO(template);
    }

    public MessageTemplateDTO updateTemplate(Long id, MessageTemplateDTO messageTemplateDTO, String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("User not found"));

        MessageTemplate template = messageTemplateRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));

        template.setTitle(messageTemplateDTO.getTitle());
        template.setBody(messageTemplateDTO.getBody());
        messageTemplateRepository.save(template);

        return mapToDTO(template);
    }

    public void deleteTemplate(Long id, String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("User not found"));

        MessageTemplate template = messageTemplateRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));

        messageTemplateRepository.delete(template);

    }

    private MessageTemplateDTO mapToDTO(MessageTemplate template) {
        return new MessageTemplateDTO(template.getId(), template.getTitle(), template.getBody());
    }
}
