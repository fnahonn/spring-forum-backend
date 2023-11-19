package com.fleo.javaforum.service;

import com.fleo.javaforum.mapper.MessageMapper;
import com.fleo.javaforum.model.Message;
import com.fleo.javaforum.model.Topic;
import com.fleo.javaforum.payload.response.MessageResponse;
import com.fleo.javaforum.repository.MessageRepository;
import com.fleo.javaforum.repository.TopicRepository;
import com.fleo.javaforum.security.model.User;
import com.fleo.javaforum.security.payload.request.MessageRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private TopicService topicService;
    @Autowired
    private MessageMapper messageMapper;

    public Iterable<MessageResponse> findAllByTopic(final long topicId) {
        List<MessageResponse> messages = messageRepository.findByTopicIdOrderByAcceptedDescCreatedAtAsc(topicId)
                .stream()
                .map(message -> messageMapper.toResponse(message))
                .toList();
        return messages;
    }

    public MessageResponse createMessage(final Long topicId, MessageRequest request, Authentication auth) {
        Topic topic = topicService.findById(topicId);
        Message message = Message.builder()
                .content(request.content())
                .topic(topic)
                .author((User) auth.getPrincipal())
                .accepted(false)
                .createdAt(Instant.now())
                .build();
        message = messageRepository.save(message);
        return messageMapper.toResponse(message);
    }

    public MessageResponse updateMessage(final int messageId, MessageRequest request) {
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
    public void deleteMessage(final int messageId) {
        Message messageToDelete = findById(messageId);
        messageRepository.delete(messageToDelete);
    }
    private Message findById(final long messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found"));
    }
}
