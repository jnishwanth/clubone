package com.firstclub.membership.catalog.web;

import com.firstclub.membership.catalog.service.PlanService;
import com.firstclub.membership.catalog.web.dto.PlanResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Public-facing: plans a user can choose from. Admin CRUD lives in {@link PlanAdminController}. */
@RestController
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    @GetMapping("/api/plans")
    public List<PlanResponse> listPublic() {
        return planService.listActive();
    }
}
