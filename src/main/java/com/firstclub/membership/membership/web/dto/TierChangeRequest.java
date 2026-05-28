package com.firstclub.membership.membership.web.dto;

import jakarta.validation.constraints.NotNull;

public record TierChangeRequest(
        @NotNull Long targetTierId
) {
}
