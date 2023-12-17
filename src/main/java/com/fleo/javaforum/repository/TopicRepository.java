package com.fleo.javaforum.repository;

import com.fleo.javaforum.model.Message;
import com.fleo.javaforum.model.Topic;
import com.fleo.javaforum.payload.response.TopicResponse;
import com.fleo.javaforum.repository.fragment.TopicRepositoryFragment;
import com.fleo.javaforum.security.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface TopicRepository extends CrudRepository<Topic, Long>, PagingAndSortingRepository<Topic, Long>, TopicRepositoryFragment {

    @EntityGraph(value = "Topic.withLastMessage", type = EntityGraph.EntityGraphType.FETCH)
    Page<Topic> findAll(Pageable pageable);

    @Override
    @EntityGraph(value = "Topic.defaultEntityGraph", type = EntityGraph.EntityGraphType.FETCH)
    Optional<Topic> findById(Long id);

    @Query(value = """
            SELECT DISTINCT u
            FROM Message m
            INNER JOIN User u ON m.author.id = u.id
            WHERE m.topic = :topic
            AND m.author != :messageAuthor
            """
    )
    Set<User> findUsersToNotify(@Param("topic") Topic topic, @Param("messageAuthor") User messageAuthor);
}
