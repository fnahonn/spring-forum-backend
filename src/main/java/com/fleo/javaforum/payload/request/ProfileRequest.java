package com.fleo.javaforum.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProfileRequest(
        @NotNull
        @Size(min = 2)
        String firstName,
        @NotNull
        @Size(min = 2)
        String lastName,
        @NotNull
        @Size(min = 2)
        String pseudo,
        @NotNull
        @Size(min = 2)
        @Email
        String email
) {
}
