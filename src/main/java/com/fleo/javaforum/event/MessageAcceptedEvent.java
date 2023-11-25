package com.fleo.javaforum.event;

import com.fleo.javaforum.model.Message;

public class MessageAcceptedEvent {

    private Message message;

    public MessageAcceptedEvent(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return this.message;
    }
}
