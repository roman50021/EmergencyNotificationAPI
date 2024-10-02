package com.fedkoroma.client.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode()
@Table(name = "app_users")
public class User {

    @Id
    protected UUID id;

    @Column(name = "email", nullable = false)
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    @Column(name = "first_name", nullable = false)
    @NotBlank(message = "First Name is mandatory")
    @Size(max = 50, message = "FirstName should not exceed 50 characters")
    private String  firstName;

    @Column(name = "last_name", nullable = false)
    @NotBlank(message = "FirstName is mandatory")
    @Size(max = 50, message = "Last Name should not exceed 50 characters")
    private String  lastName;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contact> contacts;  // Связь с контактами пользователя

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MessageTemplate> messageTemplates;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
