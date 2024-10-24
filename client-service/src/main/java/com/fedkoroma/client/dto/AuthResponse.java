package com.fedkoroma.client.dto;

import com.fedkoroma.client.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String email;
    private boolean valid;
    private Role role;
}