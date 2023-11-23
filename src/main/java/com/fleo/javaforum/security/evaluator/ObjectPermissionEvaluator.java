package com.fleo.javaforum.security.evaluator;

import com.fleo.javaforum.security.model.User;
import org.springframework.security.core.Authentication;

import java.io.Serializable;

public interface ObjectPermissionEvaluator {

    boolean supports(Object permission, String targetDomainType);
    boolean hasPermission(User user, Serializable targetId, String targetType, Object permission);
}
