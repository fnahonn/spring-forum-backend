package com.fleo.javaforum.infrastructure.messagequeue.publisher;

@Deprecated
public interface MessagePublisher {
    void publish(String topic, Object message);
}
