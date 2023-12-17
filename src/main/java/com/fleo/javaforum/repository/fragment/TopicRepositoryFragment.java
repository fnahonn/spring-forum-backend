package com.fleo.javaforum.repository.fragment;

import com.fleo.javaforum.payload.response.TopicResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

public interface TopicRepositoryFragment {
    Page<TopicResponse> search(String search, Pageable pageable);
}
