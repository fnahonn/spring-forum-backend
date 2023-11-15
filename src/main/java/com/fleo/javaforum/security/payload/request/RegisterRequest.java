package com.fleo.javaforum.security.payload.request;

import com.fleo.javaforum.security.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank
        @Size(min = 3)
        String firstName,
        @NotBlank
        @Size(min = 3)
        String lastName,
        @NotBlank
        @Size(min = 3)
        String pseudo,
        @NotNull
        @Email
        String email,
        @NotBlank
        @Size(min = 5)
        String password
) {
}
