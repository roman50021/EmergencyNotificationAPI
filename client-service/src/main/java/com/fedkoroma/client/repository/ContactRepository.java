package com.fedkoroma.client.repository;

import com.fedkoroma.client.model.Contact;
import com.fedkoroma.client.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findAllByUser(User user);
    Optional<Contact> findByIdAndUser(Long id, User user);
}
