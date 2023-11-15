package com.fleo.javaforum.security.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthenticationRequest(
        @NotBlank
        @Email
        String email,
        @NotBlank
        @Size(min = 5)
        String password
) {
}
