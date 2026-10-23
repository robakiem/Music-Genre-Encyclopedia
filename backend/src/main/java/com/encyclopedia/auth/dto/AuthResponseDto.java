package com.encyclopedia.auth.dto;

public record AuthResponseDto(
        String token,
        String username,
        String role
) {
}
