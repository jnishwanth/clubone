package com.firstclub.membership.membership.web.dto;

import jakarta.validation.constraints.NotNull;

public record SubscribeRequest(
        @NotNull Long planId,
        @NotNull Long tierId
) {
}
