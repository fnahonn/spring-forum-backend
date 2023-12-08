package com.fleo.javaforum.infrastructure.mailing;

import com.fleo.javaforum.event.MessageCreatedEvent;
import com.fleo.javaforum.infrastructure.messagequeue.message.EmailMessage;
import com.fleo.javaforum.model.Message;
import com.fleo.javaforum.security.model.User;
import jakarta.mail.MessagingException;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

@Component("forumSubscriberMailing")
public class ForumSubscriber {

    private final EmailService mailer;

    public ForumSubscriber(EmailService mailer) {
        this.mailer = mailer;
    }

    @EventListener
    public void onMessageCreated(MessageCreatedEvent event) {
        Message message = event.getMessage();
        List<User> users = List.of(message.getTopic().getAuthor());

        User author = message.getAuthor();

        for (User user : users) {
            boolean isTopicOwner = user.getId().equals(message.getTopic().getAuthor().getId());

            String subject = isTopicOwner ?
                    String.format("%s a répondu à votre sujet", author.getPseudo()) :
                    String.format("%s a répondu à un sujet auquel vous avez participé", author.getPseudo());

            Map<String, Object> params = Map.of(
                    "authorPseudo", author.getPseudo(),
                    "topicName", message.getTopic().getName(),
                    "isTopicOwner", isTopicOwner
            );
            mailer.send(new EmailMessage(
                            "new_forum_message",
                            subject,
                            message.getContent(),
                            user.getEmail(),
                            params
                    )
            );
        }
    }
}
