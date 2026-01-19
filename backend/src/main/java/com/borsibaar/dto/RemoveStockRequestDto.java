package com.borsibaar.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record RemoveStockRequestDto(
        @NotNull(message = "Product ID is required")
        Long productId,

        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.0001", message = "Removal quantity must be greater than zero")
        @Digits(integer = 12, fraction = 4)
        BigDecimal quantity,

        @Size(max = 100, message = "Reference ID is too long")
        String referenceId,

        @Size(max = 500, message = "Notes cannot exceed 500 characters")
        String notes) {
}