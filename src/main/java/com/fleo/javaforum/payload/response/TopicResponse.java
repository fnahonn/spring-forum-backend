package com.fleo.javaforum.payload.response;

import java.time.Instant;

public record TopicResponse(
        Long id,
        String name,
        String content,
        boolean solved,
        Instant createdAt,
        Instant updatedAt
) {
}
