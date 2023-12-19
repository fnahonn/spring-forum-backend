package com.fleo.javaforum.infrastructure.messagequeue.consumer;

import com.fleo.javaforum.infrastructure.mailing.EmailService;
import com.fleo.javaforum.infrastructure.messagequeue.message.EmailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;

public class EmailStreamConsumer implements StreamListener<String, ObjectRecord<String, EmailMessage>> {

    @Value("${stream.key.email:email}")
    private String streamKey;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private EmailService emailService;

    private final Logger log = LoggerFactory.getLogger(EmailStreamConsumer.class);

    @Override
    public void onMessage(ObjectRecord<String, EmailMessage> record) {

        EmailMessage emailMessage = record.getValue();
        String streamKey = record.getStream();
        RecordId recordId = record.getId();
        log.debug("RecordId {} => emailMessage will be consumed on stream {} : {}", recordId, streamKey, emailMessage);

        emailService.sendNow(emailMessage);

        redisTemplate.opsForStream().acknowledge(streamKey, streamKey, recordId);

        log.debug("RecordId {} => emailMessage acknowledged on stream {}", recordId, streamKey);
    }
}
