package com.firstclub.membership.user.web.dto;

import com.firstclub.membership.user.domain.User;

public record UserResponse(
        Long id,
        String name,
        String cohort
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getCohort());
    }
}
