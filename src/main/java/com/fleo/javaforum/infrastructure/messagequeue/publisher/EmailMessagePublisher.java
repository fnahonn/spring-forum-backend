package com.fleo.javaforum.infrastructure.messagequeue.publisher;

import com.fleo.javaforum.infrastructure.messagequeue.message.EmailMessage;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Deprecated
public class EmailMessagePublisher implements MessagePublisher {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private final Logger log = LoggerFactory.getLogger(EmailMessagePublisher.class);
    public EmailMessagePublisher() {
    }

    public EmailMessagePublisher(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void publish(String topic, Object message) {
        redisTemplate.convertAndSend(topic,(EmailMessage) message);
        log.info("Message {} published on Redis. Topic channel used : {}", message.toString(), topic);
    }
}
