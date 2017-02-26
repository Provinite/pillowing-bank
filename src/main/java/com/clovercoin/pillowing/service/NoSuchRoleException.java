package com.clovercoin.pillowing.service;

import lombok.Getter;

public class NoSuchRoleException extends RuntimeException {
    @Getter
    private String role;

    public NoSuchRoleException(String role) {
        super("No such role found: " + role);
        this.role = role;
    }

    public NoSuchRoleException(String role, String message) {
        super(message);
        this.role = role;
    }
}
