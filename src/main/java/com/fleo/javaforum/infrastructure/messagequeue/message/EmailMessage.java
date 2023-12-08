package com.fleo.javaforum.infrastructure.messagequeue.message;

import java.util.List;
import java.util.Map;

public record EmailMessage(
        String layout,
        String subject,
        String messageContent,
        String recipientAddress,
        Map<String, Object> params

) {
}
