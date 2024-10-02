package com.fedkoroma.client.service;

import com.fedkoroma.client.dto.UserDTO;
import com.fedkoroma.client.model.User;
import com.fedkoroma.client.repository.UserRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserEventListener {

    private final UserRepository userRepository;

    @Autowired
    public UserEventListener(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Обработчик события создания пользователя
    @RabbitListener(queues = "user.created.queue")
    public void handleUserCreatedEvent(UserDTO userDTO) {
        User user = mapToUser(userDTO);
        userRepository.save(user);
        System.out.println("User created event received: " + user);
    }

    // Обработчик события обновления пользователя
    @RabbitListener(queues = "user.updated.queue")
    public void handleUserUpdatedEvent(UserDTO userDTO) {
        userRepository.findById(userDTO.getId())
                .map(existingUser -> updateUser(existingUser, userDTO))
                .orElseGet(() -> {
                    System.out.println("User not found for update, ID: " + userDTO.getId());
                    return null;
                });
    }

    // Преобразование UserDTO в сущность User
    private User mapToUser(UserDTO userDTO) {
        return User.builder()
                .id(userDTO.getId())
                .email(userDTO.getEmail())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .build();
    }

    // Обновление существующего пользователя на основе UserDTO
    private User updateUser(User user, UserDTO userDTO) {
        user.setEmail(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user); // Не забывайте сохранять обновленного пользователя
        System.out.println("User updated event processed: " + user);
        return user;
    }
}
