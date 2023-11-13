package com.fleo.javaforum.service;

import com.fleo.javaforum.model.Topic;
import com.fleo.javaforum.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicService {
    @Autowired
    private TopicRepository topicRepository;

    public Page<Topic> findAll(final int page, final int size) {
        Page<Topic> topics = topicRepository.findAll(PageRequest.of(page, size, Sort.by("createdAt").descending()));
        List<Topic> topicsList = topics.stream()
                .map(TopicMapper::toResponse)
                .toList();
        return new PageImpl<>(topicsList, topics.getPageable(), topics.getSize());
    }
}
