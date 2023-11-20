package com.fleo.javaforum.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

public record TopicResponse(
        Long id,
        String name,
        String content,
        UserResponse author,
        boolean solved,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        List<MessageResponse> messages,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        MessageResponse lastMessage,
        Instant createdAt,
        Instant updatedAt
) {
}
