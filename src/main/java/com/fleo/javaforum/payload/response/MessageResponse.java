package com.fleo.javaforum.payload.response;

import com.fleo.javaforum.payload.response.TopicResponse;
import com.fleo.javaforum.payload.response.UserResponse;

import java.time.Instant;

public record MessageResponse(
        Long id,
        String content,
        TopicResponse topic,
        UserResponse author,
        Boolean accepted,
        Instant createdAt,
        Instant updatedAt
) {
}
