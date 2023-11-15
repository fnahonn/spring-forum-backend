package com.fleo.javaforum.security.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    @Column(nullable = false, unique = true)
    private String token;
    @Column(nullable = false)
    private Instant expiryDate;
    private boolean revoked;

    public RefreshToken() {
    }

    public RefreshToken(RefreshTokenBuilder builder) {
        this.id = builder.id;
        this.user = builder.user;
        this.token = builder.token;
        this.expiryDate = builder.expiryDate;
        this.revoked = builder.revoked;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public static RefreshTokenBuilder builder() { return new RefreshTokenBuilder(); }

    public static final class RefreshTokenBuilder {
        private Long id;
        private User user;
        private String token;
        private Instant expiryDate;
        private boolean revoked;

        public RefreshTokenBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public RefreshTokenBuilder user(User user) {
            this.user = user;
            return this;
        }

        public RefreshTokenBuilder token(String token) {
            this.token = token;
            return this;
        }

        public RefreshTokenBuilder expiryDate(Instant expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public RefreshTokenBuilder revoked(boolean revoked) {
            this.revoked = revoked;
            return this;
        }

        public RefreshToken build() {
            return new RefreshToken(this);
        }
    }
}
