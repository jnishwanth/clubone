package com.firstclub.membership.user.service;

import com.firstclub.membership.common.NotFoundException;
import com.firstclub.membership.user.domain.User;
import com.firstclub.membership.user.repository.UserRepository;
import com.firstclub.membership.user.web.dto.UserRequest;
import com.firstclub.membership.user.web.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponse create(UserRequest request) {
        User user = new User();
        user.setName(request.name());
        user.setCohort(request.cohort());
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserResponse get(Long id) {
        return UserResponse.from(require(id));
    }

    @Transactional
    public UserResponse updateCohort(Long id, String cohort) {
        User user = require(id);
        user.setCohort(cohort);
        return UserResponse.from(userRepository.save(user));
    }

    /** Internal: resolve a user entity for other slices within a transaction. */
    @Transactional(readOnly = true)
    public User requireById(Long id) {
        return require(id);
    }

    private User require(Long id) {
        return userRepository.findById(id).orElseThrow(() -> NotFoundException.of("User", id));
    }
}
