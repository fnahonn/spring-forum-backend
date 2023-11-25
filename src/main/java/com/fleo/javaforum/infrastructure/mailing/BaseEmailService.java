package com.fleo.javaforum.infrastructure.mailing;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    protected MimeMessage createEmail(String template, Map<String, Object> data) throws MessagingException, UnsupportedEncodingException {
        Context thymeleafContext = new Context();

        Map<String, Object> htmlData = new HashMap<>(data);
        htmlData.put("layout", "mails/base.html");
        thymeleafContext.setVariables(htmlData);
        String htmlBody = thymeleafTemplateEngine.process(template, thymeleafContext);

        Map<String, Object> textData = new HashMap<>(data);
        textData.put("layout", "mails/base.txt");
        thymeleafContext.setVariables(textData);
        String textBody = thymeleafTemplateEngine.process(template, thymeleafContext);

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");
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
