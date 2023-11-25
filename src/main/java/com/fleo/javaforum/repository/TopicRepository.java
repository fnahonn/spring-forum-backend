package com.fleo.javaforum.repository;

import com.fleo.javaforum.model.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TopicRepository extends CrudRepository<Topic, Long>, PagingAndSortingRepository<Topic, Long> {
    @EntityGraph(value = "Topic.withLastMessage", type = EntityGraph.EntityGraphType.FETCH)
    Page<Topic> findAll(Pageable pageable);

    @Override
    @EntityGraph(value = "Topic.defaultEntityGraph", type = EntityGraph.EntityGraphType.FETCH)
    Optional<Topic> findById(Long id);
}
