package com.fleo.javaforum.security.service;

import com.fleo.javaforum.security.enums.Role;
import com.fleo.javaforum.security.enums.TokenType;
import com.fleo.javaforum.security.model.User;
import com.fleo.javaforum.security.payload.request.AuthenticationRequest;
import com.fleo.javaforum.security.payload.request.RegisterRequest;
import com.fleo.javaforum.security.payload.response.AuthenticationResponse;
import com.fleo.javaforum.security.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(PasswordEncoder passwordEncoder, UserRepository userRepository, JwtService jwtService, RefreshTokenService refreshTokenService, AuthenticationManager authenticationManager) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .pseudo(request.pseudo())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();
        user = userRepository.save(user);

        var jwt = jwtService.generateToken(user);

        var refreshToken = refreshTokenService.createRefreshToken(user);

        var roles = user.getRole().getAuthorities().stream()
                .map(SimpleGrantedAuthority::getAuthority)
                .toList();
        return new AuthenticationResponse(
                user.getId(),
                user.getEmail(),
                roles,
                jwt,
                refreshToken.getToken(),
                TokenType.BEARER.name()
        );
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Email or password"));

        var jwt = jwtService.generateToken(user);

        var refreshToken = refreshTokenService.createRefreshToken(user);

        var roles = user.getRole().getAuthorities().stream()
                .map(SimpleGrantedAuthority::getAuthority)
                .toList();

        return new AuthenticationResponse(
                user.getId(),
                user.getEmail(),
                roles,
                jwt,
                refreshToken.getToken(),
                TokenType.BEARER.name()
        );
    }
}
