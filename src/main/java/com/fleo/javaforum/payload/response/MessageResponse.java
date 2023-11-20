package com.fleo.javaforum.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fleo.javaforum.payload.response.TopicResponse;
import com.fleo.javaforum.payload.response.UserResponse;

import java.time.Instant;

public record MessageResponse(
        Long id,
        String content,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Long topicId,
        UserResponse author,
        Boolean accepted,
        Instant createdAt,
        Instant updatedAt
) {
}
