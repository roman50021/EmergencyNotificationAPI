package com.fedkoroma.client.repository;

import com.fedkoroma.client.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<User, Long> {
}
