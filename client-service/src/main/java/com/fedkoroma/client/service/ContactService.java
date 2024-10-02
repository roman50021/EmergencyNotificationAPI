package com.fedkoroma.client.service;

import com.fedkoroma.client.dto.ContactDTO;
import com.fedkoroma.client.exception.EmailNotFoundException;
import com.fedkoroma.client.model.Contact;
import com.fedkoroma.client.model.User;
import com.fedkoroma.client.repository.ContactRepository;
import com.fedkoroma.client.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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


    private ContactDTO mapToDTO(Contact contact) {
        return new ContactDTO(contact.getId(), contact.getName(), contact.getPhoneNumber(), contact.getEmail());
    }
}
