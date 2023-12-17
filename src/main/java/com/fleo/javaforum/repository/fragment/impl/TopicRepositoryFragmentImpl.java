package com.fleo.javaforum.repository.fragment.impl;

import com.fleo.javaforum.mapper.TopicMapper;
import com.fleo.javaforum.model.Topic;
import com.fleo.javaforum.payload.response.TopicResponse;
import com.fleo.javaforum.repository.fragment.TopicRepositoryFragment;
import com.fleo.javaforum.security.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

public class TopicRepositoryFragmentImpl implements TopicRepositoryFragment {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private TopicMapper topicMapper;

    @Override
    public Page<TopicResponse> search(String search, Pageable pageable) {

        Query resultsQuery = em.createNativeQuery("""
            SELECT
                ft.id,
            	ts_headline('french', ft.name, websearch_to_tsquery('french', :q), 'MinWords=5, MaxWords=15, ShortWord=2,StartSel=<mark>, StopSel=</mark>') as name,
            	ts_headline('french', ft.content, websearch_to_tsquery('french', :q), 'MinWords=15, MaxWords=35, ShortWord=2,StartSel=<mark>, StopSel=</mark>') as content,
            	ft.solved,
            	ft.created_at,
            	u.pseudo,
            	u.id
            FROM forum_topic ft
            INNER JOIN users u on u.id = ft.author_id
            WHERE
            	ft.textsearchable_index_col @@ websearch_to_tsquery('french', :q)
            ORDER BY ts_rank(ft.textsearchable_index_col, websearch_to_tsquery('french', :q)) DESC;
            """
        );
        resultsQuery.setParameter("q", search);
        resultsQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        resultsQuery.setMaxResults(pageable.getPageSize());
        List results = resultsQuery.getResultList().stream()
                .map(rowData -> {
                    Object[] row = (Object[]) rowData;
                    Topic topic = Topic.builder()
                            .id((Long) row[0])
                            .name((String) row[1])
                            .content((String) row[2])
                            .solved((Boolean) row[3])
                            .createdAt((Instant) row[4])
                            .build();
                    User user = User.builder()
                            .id((Long) row[6])
                            .pseudo((String) row[5])
                            .build();
                    topic.setAuthor(user);
                    return topicMapper.toResponse(topic);
                })
                .toList();

        var totalQuery = em.createNativeQuery("""
           SELECT count(*)
           FROM forum_topic ft
           WHERE ft.textsearchable_index_col @@ websearch_to_tsquery('french', :q)
           """
        );
        totalQuery.setParameter("q", search);
        Long totalCount = (Long) totalQuery.getSingleResult();

        return new PageImpl<>(results, pageable, totalCount);

    }
}
