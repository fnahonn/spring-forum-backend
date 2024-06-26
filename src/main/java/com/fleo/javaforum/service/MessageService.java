package com.fleo.javaforum.service;

import com.fleo.javaforum.event.MessageAcceptedEvent;
import com.fleo.javaforum.event.MessageCreatedEvent;
import com.fleo.javaforum.mapper.MessageMapper;
import com.fleo.javaforum.model.Message;
import com.fleo.javaforum.model.Topic;
import com.fleo.javaforum.payload.response.MessageResponse;
import com.fleo.javaforum.repository.MessageRepository;
import com.fleo.javaforum.security.model.User;
import com.fleo.javaforum.security.payload.request.MessageRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final TopicService topicService;
    private final MessageMapper messageMapper;
    private final ApplicationEventPublisher eventPublisher;

    private final UserDetailsService userDetailsService;

    public MessageService(MessageRepository messageRepository, TopicService topicService, MessageMapper messageMapper, ApplicationEventPublisher eventPublisher, UserDetailsService userDetailsService) {
        this.messageRepository = messageRepository;
        this.topicService = topicService;
        this.messageMapper = messageMapper;
        this.eventPublisher = eventPublisher;
        this.userDetailsService = userDetailsService;
    }

    public Iterable<MessageResponse> findAllByTopic(final long topicId) {
        List<MessageResponse> messages = messageRepository.findByTopicIdOrderByAcceptedDescCreatedAtAsc(topicId)
                .stream()
                .map(message -> messageMapper.toResponse(message))
                .toList();
        return messages;
    }

    public MessageResponse createMessage(final long topicId, MessageRequest request, Authentication auth) {
        Topic topic = topicService.findById(topicId);
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User author = (User) userDetailsService.loadUserByUsername(userDetails.getUsername());

        Message message = Message.builder()
                .content(request.content())
                .topic(topic)
                .author(author)
                .accepted(false)
                .createdAt(Instant.now())
                .build();
        Message createdMessage = messageRepository.save(message);
        eventPublisher.publishEvent(new MessageCreatedEvent(createdMessage));
        return messageMapper.toResponse(createdMessage);
    }

    public MessageResponse updateMessage(final long messageId, MessageRequest request) {
        Message currentMessage = findById(messageId);
        Message hydratedMessage = Message.builder()
                .id(currentMessage.getId())
                .topic(currentMessage.getTopic())
                .author(currentMessage.getAuthor())
                .content(request.content())
                .createdAt(currentMessage.getCreatedAt())
                .updatedAt(Instant.now())
                .build();
        Message updatedMessage  = messageRepository.save(hydratedMessage);
        return messageMapper.toResponse(updatedMessage);
    }
    public void deleteMessage(final long messageId) {
        Message messageToDelete = findById(messageId);
        messageRepository.delete(messageToDelete);
    }

    public void acceptMessage(final long messageId) {
        Message message = findById(messageId);
        message.setAccepted(true);
        message.setUpdatedAt(Instant.now());
        Message acceptedMessage = messageRepository.save(message);
        eventPublisher.publishEvent(new MessageAcceptedEvent(acceptedMessage));

    }

    public Message findById(final long messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found"));
    }
}
