package com.fleo.javaforum.model;

import com.fleo.javaforum.security.model.User;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@NamedEntityGraph(
        name = "Topic.defaultEntityGraph",
        attributeNodes = {
                @NamedAttributeNode(value = "author")
        }
)
@Entity
@Table
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
