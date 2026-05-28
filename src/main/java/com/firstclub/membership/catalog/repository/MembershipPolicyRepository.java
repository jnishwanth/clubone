package com.firstclub.membership.catalog.repository;

import com.firstclub.membership.catalog.domain.MembershipPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipPolicyRepository extends JpaRepository<MembershipPolicy, Long> {
}
