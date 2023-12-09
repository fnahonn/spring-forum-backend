package com.fleo.javaforum.infrastructure.mailing;

import com.fleo.javaforum.infrastructure.messagequeue.message.EmailMessage;
import com.fleo.javaforum.infrastructure.messagequeue.publisher.EmailMessagePublisher;
import jakarta.mail.internet.InternetAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

@Service
public class EmailService {

    private final JavaMailSender emailSender;
    private final SpringTemplateEngine thymeleafTemplateEngine;
    private final EmailMessagePublisher publisher;
    private final Logger log = LoggerFactory.getLogger(EmailService.class);

    public EmailService(JavaMailSender emailSender, SpringTemplateEngine thymeleafTemplateEngine, EmailMessagePublisher publisher) {
        this.emailSender = emailSender;
        this.thymeleafTemplateEngine = thymeleafTemplateEngine;
        this.publisher = publisher;
    }

    public void send(EmailMessage message) {
        publisher.publish("email", message);
    }
    public void sendNow(EmailMessage message) {
        try {
            MimeMessagePreparator email = prepareEmail(message);
            emailSender.send(email);
            log.info("Email of type '{}' sent to : {}", message.layout(), message.recipientAddress());
        } catch (MailException e) {
            log.error("The email hasn't be sent", e);
        }
    }

    private MimeMessagePreparator prepareEmail(EmailMessage message) {
        Context thymeleafContext = new Context();

        Map<String, Object> data = message.params();

        thymeleafContext.setVariables(data);
        thymeleafContext.setVariable("layout", message.layout());
        String textBody = thymeleafTemplateEngine.process("text/base.txt", thymeleafContext);
        String htmlBody = thymeleafTemplateEngine.process("html/base.html", thymeleafContext);


        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(new InternetAddress("floflo@hotmail.fr", "Florian Nahon"));
            helper.setTo(new InternetAddress(message.recipientAddress()));
            helper.setSubject(message.subject());
            helper.setText(textBody, htmlBody);
        };

        return messagePreparator;
    }
}
