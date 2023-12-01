package com.fleo.javaforum.infrastructure.mailing;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@Service
public class BaseEmailService {

    private final JavaMailSender emailSender;

    private final SpringTemplateEngine thymeleafTemplateEngine;
    private final Logger log = LoggerFactory.getLogger(BaseEmailService.class);

    public BaseEmailService(JavaMailSender emailSender, SpringTemplateEngine thymeleafTemplateEngine) {
        this.emailSender = emailSender;
        this.thymeleafTemplateEngine = thymeleafTemplateEngine;
    }

    protected MimeMessage createEmail(String layout, Map<String, Object> data) throws MessagingException, UnsupportedEncodingException {
        Context thymeleafContext = new Context();

        thymeleafContext.setVariables(data);
        thymeleafContext.setVariable("layout", layout);
        String textBody = thymeleafTemplateEngine.process("text/base.txt", thymeleafContext);
        String htmlBody = thymeleafTemplateEngine.process("html/base.html", thymeleafContext);



        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(new InternetAddress("floflo@hotmail.fr", "Florian Nahon"));
        helper.setText(textBody, htmlBody);
        return message;
    }

    protected void send(MimeMessage mail) {
        try {
            emailSender.send(mail);
        } catch (MailException e) {
            log.error("The email hasn't be sent", e);
        }
    }

    private void send(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        emailSender.send(message);
    }
}
