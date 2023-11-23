package com.fleo.javaforum.security.evaluator;

import com.fleo.javaforum.security.model.User;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Component("customPermissionEvaluator")
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final List<ObjectPermissionEvaluator> permissionEvaluators;

    public CustomPermissionEvaluator(List<ObjectPermissionEvaluator> evaluators) {
        this.permissionEvaluators = evaluators;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        User user = (User) authentication.getPrincipal();

        for (ObjectPermissionEvaluator evaluator : permissionEvaluators) {
            if (evaluator.supports(permission, targetType)) {
                return evaluator.hasPermission(user, targetId, targetType, permission);
            }
        }
        return false;
    }
}
