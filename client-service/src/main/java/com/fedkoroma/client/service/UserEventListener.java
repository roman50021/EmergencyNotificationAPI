package com.fedkoroma.client.service;

import com.fedkoroma.client.dto.UserDTO;
import com.fedkoroma.client.model.User;
import com.fedkoroma.client.repository.UserRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserEventListener {

    @Autowired
    private UserRepository userRepository;

    // Обработчик события создания пользователя
    @RabbitListener(queues = "user.created.queue")
    public void handleUserCreatedEvent(UserDTO userDTO) {
        // Преобразование и сохранение пользователя в базе данных ClientService
        User user = mapToUser(userDTO);
        userRepository.save(user);
        System.out.println("User created event received: " + user);
    }

    // Обработчик события обновления пользователя
    @RabbitListener(queues = "user.updated.queue")
    public void handleUserUpdatedEvent(UserDTO userDTO) {
        // Поиск пользователя в базе данных ClientService
        Optional<User> optionalUser = userRepository.findById(userDTO.getId());

        if (optionalUser.isPresent()) {
            // Если пользователь найден, обновляем его данные
            User user = optionalUser.get();
            updateUser(user, userDTO);
            userRepository.save(user);
            System.out.println("User updated event processed: " + user);
        } else {
            // Если пользователь не найден, игнорируем обновление
            System.out.println("User not found for update, ID: " + userDTO.getId());
        }
    }

    // Преобразование UserDTO в сущность User
    private User mapToUser(UserDTO userDTO) {
        return User.builder()
                .id(userDTO.getId())
                .email(userDTO.getEmail())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .build();
    }

    // Обновление существующего пользователя на основе UserDTO
    private void updateUser(User user, UserDTO userDTO) {
        user.setEmail(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
    }
}
