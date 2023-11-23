package com.fleo.javaforum.security.evaluator;

import com.fleo.javaforum.model.Message;
import com.fleo.javaforum.model.Topic;
import com.fleo.javaforum.security.model.User;
import com.fleo.javaforum.service.MessageService;
import com.fleo.javaforum.service.TopicService;
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

    @Override
    public boolean supports(Object permission, String targetDomainType) {

        return PERMISSION_NAMES.contains((String) permission) && DOMAIN_TYPE_NAMES.contains(targetDomainType);

    }

    @Override
    public boolean hasPermission(User user, Serializable targetId, String targetType, Object permission) {

        String permissionString = (String) permission;

        if (!PERMISSION_NAMES.contains(permissionString)) {
            return false;
        }

        try {

            ForumPermission forumPermission = ForumPermission.valueOf((String) permission);

            return switch(forumPermission) {
                case UPDATE_TOPIC, DELETE_TOPIC -> this.canUpdateTopic(user,  topicService.findById((long) targetId));
                case UPDATE_MESSAGE, DELETE_MESSAGE -> this.ownMessage(user, messageService.findById((long) targetId));
                case SOLVE_MESSAGE -> this.canSolve(user, messageService.findById((long) targetId));
                case CREATE_MESSAGE, CREATE_TOPIC -> true;
            };

        } catch (IllegalArgumentException e) {
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
