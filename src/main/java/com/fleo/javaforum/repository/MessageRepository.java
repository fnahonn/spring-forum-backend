package com.fleo.javaforum.repository;

import com.fleo.javaforum.model.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends CrudRepository<Message, Long> {
    List<Message> findByTopicIdOrderByAcceptedDescCreatedAtAsc(final Long topicId);
}
