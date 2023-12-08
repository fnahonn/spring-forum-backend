package com.fleo.javaforum.infrastructure.messagequeue.publisher;

public interface MessagePublisher {
    void publish(String topic, Object message);
}
