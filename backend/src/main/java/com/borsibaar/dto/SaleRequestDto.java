package com.borsibaar.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record SaleRequestDto(
        @NotEmpty(message = "Sale items cannot be empty")
        @Size(max = 100, message = "Cannot process more than 100 items in a single sale")
        @Valid
        List<SaleItemRequestDto> items,

        @Size(max = 1000, message = "Notes must not exceed 1000 characters")
        String notes,

        @NotNull(message = "Bar station ID is required")
        Long barStationId) {
}