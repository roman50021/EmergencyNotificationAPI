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
public class UserDTO {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
}
