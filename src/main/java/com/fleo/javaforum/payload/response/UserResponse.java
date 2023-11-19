package com.fleo.javaforum.payload.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserResponse(
        Long id,
        String pseudo
) {
}
