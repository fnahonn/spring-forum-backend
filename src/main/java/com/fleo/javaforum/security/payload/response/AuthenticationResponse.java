package com.fleo.javaforum.security.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AuthenticationResponse(
        Long id,
        String email,
        List<String> roles,
        @JsonProperty(value = "access_token")
        String accessToken,
        @JsonProperty(value = "refresh_token")
        String refreshToken,
        @JsonProperty(value = "token_type")
        String tokenType
) {
}
