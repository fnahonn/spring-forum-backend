package com.fleo.javaforum.infrastructure.mailing;

import com.fleo.javaforum.model.Message;
import com.fleo.javaforum.security.model.User;
import com.fleo.javaforum.service.TopicService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

@Service
public class ForumEmailService extends BaseEmailService {

    private final TopicService topicService;

    public ForumEmailService(JavaMailSender emailSender, SpringTemplateEngine thymeleafTemplateEngine, TopicService topicService) {
        super(emailSender, thymeleafTemplateEngine);
        this.topicService = topicService;
    }

    public void sendAtTopicMessageCreation(Message message) throws MessagingException, UnsupportedEncodingException {
        List<User> users = List.of(message.getTopic().getAuthor());

        User author = message.getAuthor();

        for (User user : users) {
            boolean isTopicOwner = user.getId().equals(message.getTopic().getAuthor().getId());
            MimeMessage mail = createEmail("/mails/forum/new_post.txt", Map.of(
                    "author", author,
                    "message", message,
                    "topic", message.getTopic(),
                    "is_topic_owner", isTopicOwner
            ));
            MimeMessageHelper helper = new MimeMessageHelper(mail, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");
            helper.setTo(user.getEmail());
            helper.setSubject(isTopicOwner ?
                    String.format("%s a répondu à votre sujet", author.getUsername()) :
                    String.format("%s a répondu à un sujet auquel vous avez participé", author.getUsername())
            );
            this.send(mail);
        }



    }
}
