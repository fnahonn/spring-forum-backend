package com.fleo.javaforum.config;

import com.fleo.javaforum.infrastructure.messagequeue.consumer.EmailStreamConsumer;
import com.fleo.javaforum.infrastructure.messagequeue.message.EmailMessage;
import io.lettuce.core.RedisBusyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;

@Configuration
@ComponentScan
public class RedisConfig {

    private final Logger log = LoggerFactory.getLogger(RedisConfig.class);

    @Value("${stream.key.email:email}")
    private String streamKeyEmail;
    @Bean
    LettuceConnectionFactory connectionFactory() {
        RedisProperties properties = properties();
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(properties.getHost());
        configuration.setPort(properties.getPort());
        return new LettuceConnectionFactory();
    }

    public RedisProperties properties() {
        return new RedisProperties();
    }

    @Bean
    RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public StreamListener<String, ObjectRecord<String, EmailMessage>> emailStreamConsumer() {
        return new EmailStreamConsumer();
    }

    @Bean
    public Subscription startEmailSubscriber() throws UnknownHostException {

        createConsumerGroupIfNeeded(streamKeyEmail, streamKeyEmail);

         var options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                .pollTimeout(Duration.ofMillis(100))
                .targetType(EmailMessage.class)
                .build();

         var listenerContainer = StreamMessageListenerContainer.create(connectionFactory(), options);

        var subscription = listenerContainer.receive(
                Consumer.from(streamKeyEmail, InetAddress.getLocalHost().getHostName()),
                StreamOffset.create(streamKeyEmail, ReadOffset.lastConsumed()),
                emailStreamConsumer()
        );

        listenerContainer.start();
        return subscription;
    }

    private void createConsumerGroupIfNeeded(String streamKey, String consumerGroup) {
        try {
            redisTemplate().opsForStream().createGroup(streamKey, consumerGroup);
        } catch (RedisSystemException e) {
            Throwable cause = e.getRootCause();
            if (cause != null && cause.getClass().equals(RedisBusyException.class)) {
                log.info("STREAMS - Consumer group {} name already exists on stream {}", consumerGroup, streamKey);
            } else throw e;
        }

    }

}
