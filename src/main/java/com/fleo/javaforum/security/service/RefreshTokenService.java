package com.fleo.javaforum.security.service;

import com.fleo.javaforum.security.exception.TokenException;
import com.fleo.javaforum.security.enums.TokenType;
import com.fleo.javaforum.security.model.RefreshToken;
import com.fleo.javaforum.security.model.User;
import com.fleo.javaforum.security.payload.request.RefreshTokenRequest;
import com.fleo.javaforum.security.payload.response.RefreshTokenResponse;
import com.fleo.javaforum.security.repository.RefreshTokenRepository;
import com.fleo.javaforum.security.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private static final Logger log = LoggerFactory.getLogger(RefreshTokenService.class);

    @Value("${application.security.refresh-token.expiration}")
    private Long refreshExpiration;

    public RefreshTokenService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
    }

    public RefreshToken createRefreshToken(UserDetails user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes()))
                .user((User) user)
                .expiryDate(Instant.now().plusMillis(refreshExpiration))
                .revoked(false)
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshTokenResponse generateNewToken(RefreshTokenRequest request) {
        User user = refreshTokenRepository.findByToken(request.refreshToken())
                .map(this::verifyExpiration)
                .map(RefreshToken::getUser)
                .orElseThrow(() -> new TokenException(request.refreshToken(), "Refresh token doesn't exist"));

        String token = jwtService.generateToken(user);

        return new RefreshTokenResponse(
                token,
                request.refreshToken(),
                TokenType.BEARER.name()
        );
    }

    private RefreshToken verifyExpiration(RefreshToken token) {
        if (token == null) {
            log.error("Token is null");
            throw new TokenException(null, "Token is null");
        }
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new TokenException(token.getToken(), "Refresh token has expired. Please make a new authentication request");
        }
        return token;
    }
}
