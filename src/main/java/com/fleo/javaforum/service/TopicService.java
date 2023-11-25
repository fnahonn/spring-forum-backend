package com.fleo.javaforum.service;

import com.fleo.javaforum.mapper.TopicMapper;
import com.fleo.javaforum.model.Message;
import com.fleo.javaforum.model.Topic;
import com.fleo.javaforum.payload.request.TopicRequest;
import com.fleo.javaforum.payload.response.TopicResponse;
import com.fleo.javaforum.repository.MessageRepository;
import com.fleo.javaforum.repository.TopicRepository;
import com.fleo.javaforum.security.model.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class TopicService {
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private TopicMapper topicMapper;

    public Iterable<TopicResponse> findAllTopics(final int page, final int size) {
        Page<Topic> topics = topicRepository.findAll(PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return topicMapper.map(topics);
    }

    public TopicResponse findTopicById(final long id) {
        Topic topic = findById(id);
        hydrateMessages(topic);
        return topicMapper.toResponse(topic);
    }

    public TopicResponse createTopic(TopicRequest request, Authentication auth) {
        Topic topic = Topic.builder()
                .name(request.name())
                .content(request.content())
                .author((User) auth.getPrincipal())
                .solved(request.solved())
                .createdAt(Instant.now())
                .build();
        topic = topicRepository.save(topic);
        return topicMapper.toResponse(topic);
    }

    public TopicResponse updateTopic(TopicRequest request, final long id) {
        Topic currentTopic = findById(id);
        Topic hydratedTopic = Topic.builder()
                .id(currentTopic.getId())
                .name(request.name())
                .content(request.content())
                .author(currentTopic.getAuthor())
                .solved(request.solved())
                .createdAt(currentTopic.getCreatedAt())
                .updatedAt(Instant.now())
                .build();
        Topic updatedTopic = topicRepository.save(hydratedTopic);
        return topicMapper.toResponse(updatedTopic);
    }

    public void deleteTopic(final long id) {
        Topic topicToDelete = findById(id);
        topicRepository.delete(topicToDelete);
    }

    public void solveTopic(Message message) {
        Topic topic = message.getTopic();
        topic.setSolved(true);
        topic.setUpdatedAt(Instant.now());
        topicRepository.save(topic);
    }

    public Topic findById(final long id) {
        return topicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Topic not found"));
    }

    private Topic hydrateMessages(Topic topic) {
        List<Message> messages = messageRepository.findByTopicIdOrderByAcceptedDescCreatedAtAsc(topic.getId());
        topic.setMessages(messages);
        return topic;
    }
}
