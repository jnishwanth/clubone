package com.firstclub.membership.user.repository;

import com.firstclub.membership.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
