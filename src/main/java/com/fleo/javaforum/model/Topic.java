package com.fleo.javaforum.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String content;
    private boolean solved;
    private Instant createdAt;
    private Instant updatedAt;

    public Topic() {
    }

    public Topic(TopicBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.content = builder.content;
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
