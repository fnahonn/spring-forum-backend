package com.fleo.javaforum.payload.response;

import java.time.Instant;
import java.util.Set;

public record ErrorResponse(
        int status,
        Set<String> errors,
        Instant timestamp,
        String message,
        String path
) {
    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private int status;
        private Set<String> errors;
        private Instant timestamp;
        private String message;
        private String path;

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder errors(Set<String> errors) {
            this.errors = errors;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }
        public ErrorResponse build() {
            return new ErrorResponse(status, errors, timestamp, message, path);
        }
    }
}
