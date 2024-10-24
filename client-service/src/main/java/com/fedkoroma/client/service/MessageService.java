package com.fedkoroma.client.service;

import com.fedkoroma.client.dto.MessageDTO;
import com.fedkoroma.client.dto.MessageTemplateDTO;
import com.fedkoroma.client.exception.EmailNotFoundException;
import com.fedkoroma.client.model.Contact;
import com.fedkoroma.client.model.Message;
import com.fedkoroma.client.model.MessageTemplate;
import com.fedkoroma.client.model.User;
import com.fedkoroma.client.model.enums.DeliveryMethod;
import com.fedkoroma.client.model.enums.DeliveryStatus;
import com.fedkoroma.client.repository.ContactRepository;
import com.fedkoroma.client.repository.MessageRepository;
import com.fedkoroma.client.repository.MessageTemplateRepository;
import com.fedkoroma.client.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final MessageTemplateRepository templateRepository;
    private final ContactRepository contactRepository;

    // todo test and fix
    public MessageDTO createMessage(MessageDTO messageDTO, String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("User not found"));

        MessageTemplate template = templateRepository.findById(messageDTO.getTemplateId())
                .orElseThrow(() -> new RuntimeException("Template not found"));

        List<Contact> recipients = contactRepository.findAllById(messageDTO.getRecipientIds());

        if(recipients.isEmpty()){
            throw new RuntimeException("No valid recipients found");
        }

        // Преобразовать строки deliveryMethods в Set<DeliveryMethod>
        Set<DeliveryMethod> deliveryMethods = messageDTO.getDeliveryMethods().stream()
                .map(DeliveryMethod::valueOf)
                .collect(Collectors.toSet());

        Message message = Message.builder()
                .template(template)
                .recipients(recipients)
                .deliveryMethods(deliveryMethods)
                .status(DeliveryStatus.valueOf(messageDTO.getStatus())) // Статус сообщения
                .sentAt(messageDTO.getSentAt())
                .build();

        message = messageRepository.save(message);

        return mapToDTO(message);
    }

    public List<MessageDTO> getAllMessagesByUser(String email){
        return null;
    }

    public void sendMessage(Long id, String email){

    }

    public MessageDTO updateMessage(Long id,MessageDTO messageDTO, String email){
        return null;
    }

    public void deleteMessage(Long id, String email){

    }

    private MessageDTO mapToDTO(Message message) {
        return MessageDTO.builder()
                .id(message.getId())
                .templateId(message.getTemplate().getId())
                .recipientIds(
                        message.getRecipients().stream()
                                .map(Contact::getId)
                                .collect(Collectors.toList())
                )
                .deliveryMethods(
                        message.getDeliveryMethods().stream()
                                .map(Enum::name)
                                .collect(Collectors.toSet())
                )
                .status(message.getStatus().name())
                .sentAt(message.getSentAt())
                .build();
    }

}
