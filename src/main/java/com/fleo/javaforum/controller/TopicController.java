package com.fleo.javaforum.controller;

import com.fleo.javaforum.payload.request.TopicRequest;
import com.fleo.javaforum.payload.response.TopicResponse;
import com.fleo.javaforum.service.TopicService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/forum")
public class TopicController {

    private static final int ELEMENTS_PER_PAGE = 10;
    @Autowired
    private TopicService topicService;

    @GetMapping(value = "/topics", name = "topic.index")
    public ResponseEntity<Iterable<TopicResponse>> index(@RequestParam(name = "p", defaultValue = "0") final int page) {
        Iterable<TopicResponse> response = topicService.findAllTopics(page, ELEMENTS_PER_PAGE);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/topic/{id}", name = "topic.show")
    public ResponseEntity<TopicResponse> show(@PathVariable(name = "id") final long topicId) {
        TopicResponse response = topicService.findTopicById(topicId);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/topic", name = "topic.new")
    @PreAuthorize("@forumPermissionEvaluator.hasPermission(null, 'Topic', 'createTopic')")
    public ResponseEntity<TopicResponse> create(@Valid @RequestBody TopicRequest request, Authentication auth) {
        TopicResponse response = topicService.createTopic(request, auth);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping(value = "/topic/{id}", name = "topic.update")
    @PreAuthorize("@forumPermissionEvaluator.hasPermission(#id, 'Topic', 'updateTopic')")
    public ResponseEntity<TopicResponse> update(@PathVariable(name = "id") final long id,
                                                @Valid @RequestBody TopicRequest request) {
        TopicResponse response = topicService.updateTopic(request, id);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping(value = "/topic/{id}", name = "topic.delete")
    @PreAuthorize("@forumPermissionEvaluator.hasPermission(#id, 'Topic', 'deleteTopic')")
    public ResponseEntity<String> delete(@PathVariable(name = "id") final long id) {
        topicService.deleteTopic(id);
        return ResponseEntity.ok(String.format("Topic %d has been deleted successfully", id));
    }
}
