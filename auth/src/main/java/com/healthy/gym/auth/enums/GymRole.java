package com.healthy.gym.auth.enums;

public enum GymRole {
    ADMIN("ROLE_ADMIN"),
    EMPLOYEE("ROLE_EMPLOYEE"),
    TRAINER("ROLE_TRAINER"),
    USER("ROLE_USER");

    private final String role;

    GymRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
