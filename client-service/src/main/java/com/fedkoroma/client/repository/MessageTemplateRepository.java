package com.fedkoroma.client.repository;

import com.fedkoroma.client.model.MessageTemplate;
import com.fedkoroma.client.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageTemplateRepository extends JpaRepository<MessageTemplate, Long> {
    List<MessageTemplate> findAllByUser(User user);
    Optional<MessageTemplate> findByIdAndUser(Long id, User user);
}
