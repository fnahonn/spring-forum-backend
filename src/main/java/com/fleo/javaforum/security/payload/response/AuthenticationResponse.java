package com.fleo.javaforum.security.payload.response;

import java.util.List;

public record AuthenticationResponse(
        Long id,
        String email,
        List<String> roles
) {
}
