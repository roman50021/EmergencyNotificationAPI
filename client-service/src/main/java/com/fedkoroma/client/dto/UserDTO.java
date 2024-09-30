package com.fedkoroma.client.dto;

import com.fedkoroma.client.model.Role;
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
public class UserDTO {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDateTime registeredAt;
    private Role role;
    private Boolean locked;
    private Boolean enabled;
}
