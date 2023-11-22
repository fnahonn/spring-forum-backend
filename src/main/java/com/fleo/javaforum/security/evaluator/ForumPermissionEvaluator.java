package com.fleo.javaforum.security.evaluator;

import com.fleo.javaforum.model.Message;
import com.fleo.javaforum.model.Topic;
import com.fleo.javaforum.security.model.User;
import com.fleo.javaforum.service.MessageService;
import com.fleo.javaforum.service.TopicService;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Objects;

@Component("forumPermissionEvaluator")
public class ForumPermissionEvaluator implements PermissionEvaluator {

    private static final String CREATE_TOPIC = "createTopic";
    private static final String UPDATE_TOPIC = "updateTopic";
    private static final String DELETE_TOPIC = "deleteTopic";
    private static final String CREATE_MESSAGE = "createMessage";
    private static final String UPDATE_MESSAGE = "updateMessage";
    private static final String DELETE_MESSAGE = "deleteMessage";
    private static final String SOLVE_MESSAGE = "solveMessage";

    private final TopicService topicService;
    private final MessageService messageService;

    public ForumPermissionEvaluator(TopicService topicService, MessageService messageService) {
        this.topicService = topicService;
        this.messageService = messageService;
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

        Object targetDomainObject = switch(targetType) {
            case "Topic" -> topicService.findById((long) targetId);
            case "Message" -> messageService.findById((long) targetId);
            default -> null;
        };


        return switch((String) permission) {
            case UPDATE_TOPIC, DELETE_TOPIC -> {
                assert targetDomainObject instanceof Topic;
                yield this.canUpdateTopic(user, (Topic) targetDomainObject);
            }
            case UPDATE_MESSAGE, DELETE_MESSAGE -> {
                assert targetDomainObject instanceof Message;
                yield this.ownMessage(user, (Message) targetDomainObject);
            }
            case SOLVE_MESSAGE -> {
                assert targetDomainObject instanceof Message;
                yield this.canSolve(user, (Message) targetDomainObject);
            }
            case CREATE_MESSAGE, CREATE_TOPIC -> true;
            default -> false;
        };
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
