package com.borsibaar.dto;

public record MeResponseDto(String email, String name, String role, Long organizationId, boolean needsOnboarding) {
}