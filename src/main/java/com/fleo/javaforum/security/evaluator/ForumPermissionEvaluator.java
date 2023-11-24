package com.fleo.javaforum.security.evaluator;

import com.fleo.javaforum.model.Message;
import com.fleo.javaforum.model.Topic;
import com.fleo.javaforum.security.model.User;
import com.fleo.javaforum.service.MessageService;
import com.fleo.javaforum.service.TopicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Component
public class ForumPermissionEvaluator implements ObjectPermissionEvaluator {

    public enum ForumPermission {
        CREATE_TOPIC,
        UPDATE_TOPIC,
        DELETE_TOPIC,
        CREATE_MESSAGE,
        UPDATE_MESSAGE,
        DELETE_MESSAGE,
        SOLVE_MESSAGE
    }
    public enum ForumDomainTypeSupported {
        TOPIC,
        MESSAGE,
    }

    private final Set<String> PERMISSION_NAMES = EnumSet.allOf(ForumPermission.class)
            .stream()
            .map(Enum::name)
            .collect(Collectors.toSet());

    private final Set<String> DOMAIN_TYPE_NAMES = EnumSet.allOf(ForumDomainTypeSupported.class)
            .stream()
            .map(Enum::name)
            .collect(Collectors.toSet());

    @Autowired
    private TopicService topicService;
    @Autowired
    private MessageService messageService;
    private final Logger log = LoggerFactory.getLogger(ForumPermissionEvaluator.class);

    @Override
    public boolean supports(Object permission, String targetDomainType) {

        return PERMISSION_NAMES.contains((String) permission) && DOMAIN_TYPE_NAMES.contains(targetDomainType.toUpperCase());

    }

    @Override
    public boolean hasPermission(User user, Serializable targetId, String targetType, Object permission) {

        String permissionString = (String) permission;

        if (!PERMISSION_NAMES.contains(permissionString)) {
            return false;
        }

        Topic topic = null;
        Message message = null;
        if (targetId != null) {
            if (targetType.equalsIgnoreCase(ForumDomainTypeSupported.TOPIC.name())) {
                topic = topicService.findById((Long) targetId);
            } else if (targetType.equalsIgnoreCase(ForumDomainTypeSupported.MESSAGE.name())) {
                message = messageService.findById((Long) targetId);
            }
        }

        try {

            ForumPermission forumPermission = ForumPermission.valueOf((String) permission);

            return switch(forumPermission) {
                case UPDATE_TOPIC, DELETE_TOPIC -> (topic != null) && this.canUpdateTopic(user,  topic);
                case UPDATE_MESSAGE, DELETE_MESSAGE -> (message != null) && this.ownMessage(user, message);
                case SOLVE_MESSAGE -> (message != null) && this.canSolve(user, message);
                case CREATE_MESSAGE, CREATE_TOPIC -> true;
            };

        } catch (IllegalArgumentException e) {
            log.error("Cannot map permission {} to an existing ForumPermission enum value. Reason : {}", permission, e.getMessage());
            return false;
        }
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
