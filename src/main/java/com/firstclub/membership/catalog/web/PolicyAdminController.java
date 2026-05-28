package com.firstclub.membership.catalog.web;

import com.firstclub.membership.catalog.service.PolicyService;
import com.firstclub.membership.catalog.web.dto.PolicyDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Admin: tune the global membership policy (currently the grace window in days). */
@RestController
@RequestMapping("/api/admin/policy")
@RequiredArgsConstructor
public class PolicyAdminController {

    private final PolicyService policyService;

    @GetMapping
    public PolicyDto get() {
        return policyService.get();
    }

    @PutMapping
    public PolicyDto update(@Valid @RequestBody PolicyDto dto) {
        return policyService.update(dto);
    }
}
