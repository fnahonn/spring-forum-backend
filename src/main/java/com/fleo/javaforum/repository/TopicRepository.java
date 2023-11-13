package com.fleo.javaforum.repository;

import com.fleo.javaforum.model.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends CrudRepository<Topic, Long>, PagingAndSortingRepository<Topic, Long> {
    Page<Topic> findAll();
}
