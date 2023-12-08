package com.fleo.javaforum.config;

import com.fleo.javaforum.infrastructure.messagequeue.consumer.EmailMessageConsumer;
import com.fleo.javaforum.infrastructure.messagequeue.publisher.EmailMessagePublisher;
import com.fleo.javaforum.infrastructure.messagequeue.publisher.MessagePublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@ComponentScan
public class RedisConfig {

    @Bean
    LettuceConnectionFactory connectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean
    RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory());
        template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        template.setStringSerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    EmailMessageConsumer emailMessageConsumer() {
        return new EmailMessageConsumer();
    }
    @Bean
    MessageListenerAdapter emailMessageListenerAdapter() {
        return new MessageListenerAdapter(emailMessageConsumer());
    }

    @Bean
    MessagePublisher redisPublisher() {
        return new EmailMessagePublisher(redisTemplate());
    }

    @Bean
    RedisMessageListenerContainer redisMessageListenerContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.addMessageListener(emailMessageListenerAdapter(), ChannelTopic.of("email"));
        return container;
    }
}
