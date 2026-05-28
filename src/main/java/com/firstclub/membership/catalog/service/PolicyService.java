package com.firstclub.membership.catalog.service;

import com.firstclub.membership.catalog.domain.MembershipPolicy;
import com.firstclub.membership.catalog.repository.MembershipPolicyRepository;
import com.firstclub.membership.catalog.web.dto.PolicyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Manages the single global policy row (grace window). */
@Service
@RequiredArgsConstructor
public class PolicyService {

    private final MembershipPolicyRepository policyRepository;

    @Transactional
    public MembershipPolicy getOrCreate() {
        return policyRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> policyRepository.save(new MembershipPolicy()));
    }

    @Transactional(readOnly = true)
    public PolicyDto get() {
        return policyRepository.findAll().stream()
                .findFirst()
                .map(p -> new PolicyDto(p.getGraceWindowDays()))
                .orElse(new PolicyDto(new MembershipPolicy().getGraceWindowDays()));
    }

    @Transactional
    public PolicyDto update(PolicyDto dto) {
        MembershipPolicy policy = getOrCreate();
        policy.setGraceWindowDays(dto.graceWindowDays());
        policyRepository.save(policy);
        return new PolicyDto(policy.getGraceWindowDays());
    }

    @Transactional(readOnly = true)
    public int graceWindowDays() {
        return get().graceWindowDays();
    }
}
