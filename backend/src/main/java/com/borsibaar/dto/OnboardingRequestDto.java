package com.borsibaar.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

public record OnboardingRequestDto(
        @NotNull(message = "Organization ID is required")
        Long organizationId,

        @AssertTrue(message = "You must accept the terms")
        boolean acceptTerms
) {}