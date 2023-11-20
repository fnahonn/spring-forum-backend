package com.fleo.javaforum.model;

import com.fleo.javaforum.security.model.User;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.List;

@NamedEntityGraph(
        name = "Topic.defaultEntityGraph",
        attributeNodes = {
                @NamedAttributeNode(value = "author")
        }
)
@Entity
@Table(name = "forum_topic")
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User author;
    @Column(nullable = false)
    private boolean solved;
    @OneToMany(targetEntity = Message.class, mappedBy = "topic", fetch = FetchType.LAZY)
    private List<Message> messages;
    @ManyToOne(targetEntity = Message.class, fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Message lastMessage;
    @Column(nullable = false)
    private Instant createdAt;
    private Instant updatedAt;

    public Topic() {
    }

    public Topic(TopicBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.content = builder.content;
        this.author = builder.author;
        this.solved = builder.solved;
        this.messages = builder.messages;
        this.lastMessage = builder.lastMessage;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    public Long getId() {
        return this.id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
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

    public static TopicBuilder builder() { return new TopicBuilder(); }

    public static final class TopicBuilder {
        private Long id;
        private String name;
        private String content;
        private User author;
        private boolean solved;
        private List<Message> messages;
        private Message lastMessage;
        private Instant createdAt;
        private Instant updatedAt;

        public TopicBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public TopicBuilder name(String name) {
            this.name = name;
            return this;
        }

        public TopicBuilder content(String content) {
            this.content = content;
            return this;
        }

        public TopicBuilder author(User author) {
            this.author = author;
            return this;
        }

        public TopicBuilder solved(boolean solved) {
            this.solved = solved;
            return this;
        }

        public TopicBuilder messages(List<Message> messages) {
            this.messages = messages;
            return this;
        }

        public TopicBuilder lastMessage(Message lastMessage) {
            this.lastMessage = lastMessage;
            return this;
        }

        public TopicBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public TopicBuilder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Topic build() {
            return new Topic(this);
        }
    }
}
