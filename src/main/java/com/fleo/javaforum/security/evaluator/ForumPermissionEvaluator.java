package com.fleo.javaforum.security.evaluator;

import com.fleo.javaforum.model.Message;
import com.fleo.javaforum.model.Topic;
import com.fleo.javaforum.security.model.User;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;
import java.util.Objects;

public class ForumPermissionEvaluator implements PermissionEvaluator {

    private static final String CREATE_TOPIC = "createTopic";
    private static final String UPDATE_TOPIC = "updateTopic";
    private static final String DELETE_TOPIC = "deleteTopic";
    private static final String CREATE_MESSAGE = "createMessage";
    private static final String UPDATE_MESSAGE = "updateMessage";
    private static final String DELETE_MESSAGE = "deleteMessage";
    private static final String SOLVE_MESSAGE = "solveMessage";


    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        User user = (User) authentication.getPrincipal();

        return switch((String) permission) {
            case UPDATE_TOPIC, DELETE_TOPIC -> this.canUpdateTopic(user, (Topic) targetDomainObject);
            case UPDATE_MESSAGE, DELETE_MESSAGE -> this.ownMessage(user, (Message) targetDomainObject);
            case SOLVE_MESSAGE -> this.canSolve(user, (Message) targetDomainObject);
            case CREATE_MESSAGE, CREATE_TOPIC -> true;
            default -> false;
        };
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }

    private boolean canUpdateTopic(User user, Topic topic) {
        return Objects.equals(topic.getAuthor().getId(), user.getId());
    }

    private boolean ownMessage(User user, Message message) {
        return Objects.equals(message.getAuthor().getId(), user.getId());
    }

    private boolean canSolve(User user, Message message) {
        return Objects.equals(message.getTopic().getAuthor().getId(), user.getId());

    }
}
