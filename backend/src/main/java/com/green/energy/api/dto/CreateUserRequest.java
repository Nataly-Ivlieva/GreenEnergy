package com.green.energy.api.dto;

public record CreateUserRequest(
        String username,
        String password,
        String role
) {}
