package com.fleo.javaforum.infrastructure.messagequeue.consumer;

import com.fleo.javaforum.infrastructure.mailing.EmailService;
import com.fleo.javaforum.infrastructure.messagequeue.message.EmailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;

public class EmailMessageConsumer implements MessageListener {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private EmailService emailService;

    private final Logger log = LoggerFactory.getLogger(EmailMessageConsumer.class);

    @Override
    public void onMessage(Message message, byte[] pattern) {
        EmailMessage email = (EmailMessage) redisTemplate.getValueSerializer().deserialize(message.getBody());
        String channel = redisTemplate.getStringSerializer().deserialize(message.getChannel());
        log.info("GET EmailMessage {} from Redis Channel '{}'", email, channel);
        emailService.sendNow(email);
    }
}
