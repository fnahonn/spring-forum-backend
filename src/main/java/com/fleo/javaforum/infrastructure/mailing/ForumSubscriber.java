package com.fleo.javaforum.infrastructure.mailing;

import com.fleo.javaforum.event.MessageCreatedEvent;
import jakarta.mail.MessagingException;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component("forumSubscriberMailing")
public class ForumSubscriber {

    private final ForumEmailService mailer;

    public ForumSubscriber(ForumEmailService mailer) {
        this.mailer = mailer;
    }

    @EventListener
    public void onMessageCreated(MessageCreatedEvent event) throws MessagingException, UnsupportedEncodingException {
        mailer.sendAtTopicMessageCreation(event.getMessage());
    }
}
