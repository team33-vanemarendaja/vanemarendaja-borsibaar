package com.borsibaar.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;

public record AdjustStockRequestDto(
        @NotNull(message = "Product ID is required")
        Long productId,

        @NotNull(message = "New quantity is required")
        @DecimalMin(value = "0.0", message = "New quantity cannot be negative")
        @Digits(integer = 12, fraction = 4, message = "Quantity format out of bounds")
        BigDecimal newQuantity,

        @Size(max = 500, message = "Notes cannot exceed 500 characters")
        String notes) {
}