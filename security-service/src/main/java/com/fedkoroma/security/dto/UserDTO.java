package com.fedkoroma.security.dto;

import com.fedkoroma.security.model.Role;
import com.fedkoroma.security.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
///For ClientService
public class UserDTO {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;

    // Метод преобразования User в UserDTO
    public static UserDTO fromUser(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }
}
