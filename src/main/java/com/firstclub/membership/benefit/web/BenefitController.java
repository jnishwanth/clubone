package com.firstclub.membership.benefit.web;

import com.firstclub.membership.benefit.service.EntitlementService;
import com.firstclub.membership.benefit.web.dto.BenefitPreviewResponse;
import com.firstclub.membership.benefit.web.dto.CartPreviewRequest;
import com.firstclub.membership.catalog.web.dto.BenefitDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BenefitController {

    private final EntitlementService entitlementService;

    @GetMapping("/api/users/{userId}/benefits")
    public List<BenefitDto> benefits(@PathVariable Long userId) {
        return entitlementService.activeBenefits(userId);
    }

    @PostMapping("/api/users/{userId}/benefits/preview")
    public BenefitPreviewResponse preview(@PathVariable Long userId,
                                          @Valid @RequestBody CartPreviewRequest request) {
        return entitlementService.preview(userId, request);
    }
}
