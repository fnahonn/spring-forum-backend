package com.fleo.javaforum.model;

import com.fleo.javaforum.security.model.User;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Entity
@Table(name = "forum_message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(targetEntity = Topic.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Topic topic;
    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User author;
    @Column
    private boolean accepted = false;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    private Instant createdAt;
    private Instant updatedAt;

    public Message() {
    }

    public Message(MessageBuilder builder) {
        this.id = builder.id;
        this.topic = builder.topic;
        this.author = builder.author;
        this.accepted = builder.accepted;
        this.content = builder.content;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static MessageBuilder builder() { return new MessageBuilder();}

    public static final class MessageBuilder {
        private Long id;
        private Topic topic;
        private User author;
        private String content;
        private boolean accepted;
        private Instant createdAt;
        private Instant updatedAt;

        public MessageBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public MessageBuilder topic(Topic topic) {
            this.topic = topic;
            return this;
        }

        public MessageBuilder author(User author) {
            this.author = author;
            return this;
        }

        public MessageBuilder content(String content) {
            this.content = content;
            return this;
        }

        public MessageBuilder accepted(boolean accepted) {
            this.accepted = accepted;
            return this;
        }

        public MessageBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public MessageBuilder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Message build() {
            return new Message(this);
        }
    }
}
