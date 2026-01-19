package com.borsibaar.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public record BarStationRequestDto(
        @NotBlank(message = "Bar station name is required")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name,

        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description,

        @NotNull(message = "Active status must be specified")
        Boolean isActive,

        @NotNull(message = "User list cannot be null")
        List<UUID> userIds
) {
}