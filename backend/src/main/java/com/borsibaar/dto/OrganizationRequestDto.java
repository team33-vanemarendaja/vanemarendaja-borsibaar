package com.borsibaar.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record OrganizationRequestDto(
        @NotBlank(message = "Organization name is required")
        @Size(max = 100, message = "Organization name cannot exceed 100 characters")
        String name,

        @NotNull(message = "Price increase step is required")
        @DecimalMin(value = "0.01", message = "Price increase step must be at least 0.01")
        @Digits(integer = 10, fraction = 2)
        BigDecimal priceIncreaseStep,

        @NotNull(message = "Price decrease step is required")
        @DecimalMin(value = "0.01", message = "Price decrease step must be at least 0.01")
        @Digits(integer = 10, fraction = 2)
        BigDecimal priceDecreaseStep) {
}