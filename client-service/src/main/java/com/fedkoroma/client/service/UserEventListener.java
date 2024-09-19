package com.fedkoroma.client.service;

import com.fedkoroma.client.dto.UserDTO;
import com.fedkoroma.client.model.User;
import com.fedkoroma.client.repository.UserRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserEventListener {

    @Autowired
    private UserRepository userRepository;

    @RabbitListener(queues = "user.created.queue")
    public void handleUserCreated(UserDTO userDTO) {
        // Логика обработки события создания пользователя
        System.out.println("User created: " + userDTO);
        // Преобразование DTO в сущность User
        User user = mapToUser(userDTO);
        userRepository.save(user);  // Сохранение в базе данных
    }

    @RabbitListener(queues = "user.updated.queue")
    public void handleUserUpdated(UserDTO userDTO) {
        // Логика обработки события обновления пользователя
        System.out.println("User updated: " + userDTO);
        // Преобразование DTO в сущность User
        User user = mapToUser(userDTO);
        userRepository.save(user);  // Обновление в базе данных
    }

    // Метод преобразования UserDTO в User
    private User mapToUser(UserDTO userDTO) {
        return User.builder()
                .id(userDTO.getId())
                .email(userDTO.getEmail())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .registeredAt(userDTO.getRegisteredAt())
                .role(userDTO.getRole())
                .locked(userDTO.getLocked())
                .enabled(userDTO.getEnabled())
                .build();
    }
}
