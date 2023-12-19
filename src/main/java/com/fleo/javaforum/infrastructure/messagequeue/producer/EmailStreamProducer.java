package com.fleo.javaforum.infrastructure.messagequeue.producer;

import com.fleo.javaforum.infrastructure.messagequeue.message.EmailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailStreamProducer {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${stream.key.email:email}")
    private String streamKey;
    public EmailStreamProducer(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Logger log = LoggerFactory.getLogger(EmailStreamProducer.class);

    public RecordId produce(EmailMessage message) {
        ObjectRecord<String, EmailMessage> record = StreamRecords.newRecord()
                .ofObject(message)
                .withStreamKey(streamKey);

        RecordId recordId = redisTemplate
                .opsForStream()
                .add(record);

        log.debug("Record with RecordId {} added to Redis stream with key {}. Message sent : {}", recordId, streamKey, message);

        return recordId;
    }
}
