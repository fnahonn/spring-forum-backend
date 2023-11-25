package com.fleo.javaforum.controller;

import com.fleo.javaforum.payload.response.MessageResponse;
import com.fleo.javaforum.security.payload.request.MessageRequest;
import com.fleo.javaforum.service.MessageService;
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
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/topics/{id}/messages")
    @PreAuthorize("hasPermission(null, 'MESSAGE', 'CREATE_MESSAGE')")
    public ResponseEntity<MessageResponse> create(@PathVariable(name = "id") final long topicId,
                                                  @Valid @RequestBody MessageRequest request,
                                                  Authentication auth) {
        MessageResponse response = messageService.createMessage(topicId, request, auth);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/messages/{id}")
    @PreAuthorize("hasPermission(#messageId, 'MESSAGE', 'UPDATE_MESSAGE')")
    public ResponseEntity<MessageResponse> update(@PathVariable(name = "id") final long messageId,
                                                  @Valid @RequestBody MessageRequest request) {
        MessageResponse response = messageService.updateMessage(messageId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/messages/{id}")
    @PreAuthorize("hasPermission(#messageId, 'MESSAGE', 'DELETE_MESSAGE')")
    public ResponseEntity<String> delete(@PathVariable(name = "id") final long messageId) {
        messageService.deleteMessage(messageId);
        return ResponseEntity.ok(String.format("Message %d has been deleted successfully", messageId));
    }

    @PostMapping("/messages/{id}/solve")
    @PreAuthorize("hasPermission(#messageId, 'MESSAGE', 'SOLVE_MESSAGE')")
    public ResponseEntity<String> solve(@PathVariable(name = "id") final long messageId) {
        messageService.acceptMessage(messageId);
        return ResponseEntity.ok(String.format("Message %d and associated Topic have been marked as solved successfully", messageId));
    }
}
