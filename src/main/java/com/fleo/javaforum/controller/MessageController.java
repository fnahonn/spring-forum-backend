package com.fleo.javaforum.controller;

import com.fleo.javaforum.payload.response.MessageResponse;
import com.fleo.javaforum.security.payload.request.MessageRequest;
import com.fleo.javaforum.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/forum")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/topics/{id}/messages")
    public ResponseEntity<MessageResponse> create(@PathVariable(name = "id") final long topicId,
                                                  @Valid @RequestBody MessageRequest request,
                                                  Authentication auth) {
        MessageResponse response = messageService.createMessage(topicId, request, auth);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/messages/{id}")
    public ResponseEntity<MessageResponse> update(@PathVariable(name = "id") final long messageId,
                                                  @Valid @RequestBody MessageRequest request) {
        MessageResponse response = messageService.updateMessage(messageId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/messages/{id}")
    public ResponseEntity<String> delete(@PathVariable(name = "id") final long messageId) {
        messageService.deleteMessage(messageId);
        return ResponseEntity.ok(String.format("Message %d has been deleted successfully", messageId));
    }
}
