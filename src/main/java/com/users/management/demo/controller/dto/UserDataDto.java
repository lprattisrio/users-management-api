package com.users.management.demo.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserDataDto(
        Long id,
        @NotBlank(message = "Name cannot be blank")
        @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
        String name,
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "email must be valid")
        String email,
        @NotBlank(message = "Role cannot be blank")
        String role) {
}
