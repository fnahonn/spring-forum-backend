package com.fleo.javaforum.event;

import com.fleo.javaforum.model.Message;

public class MessageCreatedEvent {
    private Message message;

    public MessageCreatedEvent(Message message) {
        this.message = message;
    }
    public Message getMessage() {
        return message;
    }
}
