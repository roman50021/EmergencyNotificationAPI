package com.fedkoroma.security.dto;

import com.fedkoroma.security.model.Role;
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
