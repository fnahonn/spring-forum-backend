package com.fleo.javaforum.security.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MessageRequest(
        @NotBlank
        @Size(min = 3)
        String content
) {
}
