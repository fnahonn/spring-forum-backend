package com.fleo.javaforum.listener;

import com.fleo.javaforum.event.MessageAcceptedEvent;
import com.fleo.javaforum.service.TopicService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ForumSubscriber {

    private final TopicService topicService;

    public ForumSubscriber(TopicService topicService) {
        this.topicService = topicService;
    }
    @EventListener
    public void onMessageAccepted(MessageAcceptedEvent event) {
        topicService.solveTopic(event.getMessage());
    }

}
