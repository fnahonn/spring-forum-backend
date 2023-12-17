package com.fleo.javaforum.repository.fragment;

import com.fleo.javaforum.model.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TopicRepositoryFragment {

    /**
     * Search a list of topics based on the fulltext query string provided
     * @param search query string
     * @param pageable pageable
     * @return a paginated list of topics based on the query string provided
     */
    Page<Topic> search(String search, Pageable pageable);
}
