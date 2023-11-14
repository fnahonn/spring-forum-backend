package com.fleo.javaforum.security.enums;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public enum Role {
    ADMIN,
    USER;

    public List<SimpleGrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + this.name());
        return new ArrayList<>(Arrays.asList(authority));
    }
}
