package com.firstclub.membership.user.web;

import com.firstclub.membership.user.service.UserService;
import com.firstclub.membership.user.web.dto.CohortUpdateRequest;
import com.firstclub.membership.user.web.dto.UserRequest;
import com.firstclub.membership.user.web.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/api/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody UserRequest request) {
        return userService.create(request);
    }

    @GetMapping("/api/users/{id}")
    public UserResponse get(@PathVariable Long id) {
        return userService.get(id);
    }

    @PatchMapping("/api/users/{id}/cohort")
    public UserResponse updateCohort(@PathVariable Long id, @RequestBody CohortUpdateRequest request) {
        return userService.updateCohort(id, request.cohort());
    }
}
