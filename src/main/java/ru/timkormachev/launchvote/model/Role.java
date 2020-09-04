package ru.timkormachev.launchvote.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER,
    ADMIN;

    @Override
    //    https://stackoverflow.com/a/19542316/548473
    public String getAuthority() {
        return "ROLE_" + name();
    }
}
