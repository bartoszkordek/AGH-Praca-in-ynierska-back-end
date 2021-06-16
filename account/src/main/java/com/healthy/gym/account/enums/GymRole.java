package com.healthy.gym.account.enums;

import org.springframework.security.core.GrantedAuthority;

public enum GymRole implements GrantedAuthority {
    ADMIN("ROLE_ADMIN"),
    EMPLOYEE("ROLE_EMPLOYEE"),
    MANAGER("ROLE_MANAGER"),
    TRAINER("ROLE_TRAINER"),
    USER("ROLE_USER");

    private final String role;

    GymRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return this.role;
    }

    @Override
    public String getAuthority() {
        return this.role;
    }

    @Override
    public String toString() {
        return this.role;
    }
}
