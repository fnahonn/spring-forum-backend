package com.fleo.javaforum.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

@Configuration
@ComponentScan
public class ThymeleafConfig implements ApplicationContextAware {

    private final String EMAIL_TEMPLATE_ENCODING = "UTF-8";

    private ApplicationContext applicationContext;

    public ThymeleafConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public ResourceBundleMessageSource emailMessageSource() {
        final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("emails/MailMessages");
        return messageSource;
    }
    @Bean
    public SpringTemplateEngine emailTemplateEngine() {
        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        // Resolver for TEXT emails
        templateEngine.addTemplateResolver(textTemplateResolver());
        // Resolver for HTML emails (except the editable one)
        templateEngine.addTemplateResolver(htmlTemplateResolver());
        // Message source, internationalization specific to emails
        templateEngine.setTemplateEngineMessageSource(emailMessageSource());
        return templateEngine;
    }

    private ITemplateResolver textTemplateResolver() {
        final SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setOrder(Integer.valueOf(1));
        templateResolver.setApplicationContext(this.applicationContext);
        templateResolver.setPrefix("classpath:/templates/emails/");
        templateResolver.setSuffix(".txt");
        templateResolver.setTemplateMode(TemplateMode.TEXT);
        templateResolver.setCharacterEncoding(EMAIL_TEMPLATE_ENCODING);
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    private ITemplateResolver htmlTemplateResolver() {
        final SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setOrder(Integer.valueOf(2));
        templateResolver.setApplicationContext(this.applicationContext);
        templateResolver.setPrefix("classpath:/templates/emails/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(EMAIL_TEMPLATE_ENCODING);
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
