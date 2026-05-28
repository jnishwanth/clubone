package com.firstclub.membership.user.web.dto;

import jakarta.validation.constraints.NotBlank;

public record UserRequest(
        @NotBlank String name,
        String cohort
) {
}
