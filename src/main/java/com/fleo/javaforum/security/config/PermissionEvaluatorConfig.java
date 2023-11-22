package com.fleo.javaforum.security.config;

import com.fleo.javaforum.security.evaluator.ForumPermissionEvaluator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class PermissionEvaluatorConfig {

    @Bean
    static MethodSecurityExpressionHandler expressionHandler(ForumPermissionEvaluator forumPermissionEvaluator) {
        var expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(forumPermissionEvaluator);
        return expressionHandler;
    }
}
