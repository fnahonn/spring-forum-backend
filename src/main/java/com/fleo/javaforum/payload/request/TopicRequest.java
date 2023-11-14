package com.fleo.javaforum.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record TopicRequest(
        @NotBlank
        @Size(min = 3, max = 100)
        String name,
        @NotBlank
        @Size(min = 1)
        String content,
        @NotBlank
        Boolean solved
) {
    public TopicRequest {
        solved = (solved != null) ? solved : false;
    }
}
