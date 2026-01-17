package com.borsibaar.dto;

public record AuthResultDto(UserDTO dto, boolean needsOnboarding) {
}