package com.fedkoroma.client.service;

import com.fedkoroma.client.dto.ContactDTO;
import com.fedkoroma.client.exception.EmailNotFoundException;
import com.fedkoroma.client.exception.ResourceNotFoundException;
import com.fedkoroma.client.model.Contact;
import com.fedkoroma.client.model.User;
import com.fedkoroma.client.repository.ContactRepository;
import com.fedkoroma.client.repository.UserRepository;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final UserRepository userRepository;

    public ContactDTO createContact(ContactDTO contactDTO, String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("User not found"));

        Contact contact = new Contact();
        contact.setName(contactDTO.getName());
        contact.setPhoneNumber(contactDTO.getPhoneNumber());
        contact.setEmail(contactDTO.getEmail());
        contact.setUser(user);
        contactRepository.save(contact);

        return mapToDTO(contact);
    }

    public List<ContactDTO> getAllContactsByUser(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("User not found"));

        return contactRepository.findAllByUser(user)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ContactDTO getContactByIdAndUser(Long id, String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("User not found"));

        Contact contact = contactRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));

        return mapToDTO(contact);
    }

    public ContactDTO updateContact(Long id, ContactDTO contactDTO, String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("User not found"));

        Contact contact = contactRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));

        contact.setName(contactDTO.getName());
        contact.setPhoneNumber(contactDTO.getPhoneNumber());
        contact.setEmail(contactDTO.getEmail());
        contactRepository.save(contact);

        return mapToDTO(contact);
    }

    public void deleteContact(Long id, String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("User not found"));

        Contact contact = contactRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));

        contactRepository.delete(contact);

    }

    private ContactDTO mapToDTO(Contact contact) {
        return new ContactDTO(contact.getId(), contact.getName(), contact.getPhoneNumber(), contact.getEmail());
    }
}
