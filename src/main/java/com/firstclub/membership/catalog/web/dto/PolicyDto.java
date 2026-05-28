package com.firstclub.membership.catalog.web.dto;

import jakarta.validation.constraints.PositiveOrZero;

public record PolicyDto(
        @PositiveOrZero int graceWindowDays
) {
}
