package com.fleo.javaforum.security.enums;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

public enum Role {
    ADMIN,
    USER;

    public Set<SimpleGrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + this.name());
        return new HashSet<>(Set.of(authority));
    }
}
