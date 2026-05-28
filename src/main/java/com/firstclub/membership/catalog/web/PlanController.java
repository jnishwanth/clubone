package com.firstclub.membership.catalog.web;

import com.firstclub.membership.catalog.service.PlanService;
import com.firstclub.membership.catalog.web.dto.PlanRequest;
import com.firstclub.membership.catalog.web.dto.PlanResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    /** Public: plans a user can choose from. */
    @GetMapping("/api/plans")
    public List<PlanResponse> listPublic() {
        return planService.listActive();
    }

    @GetMapping("/api/admin/plans")
    public List<PlanResponse> listAll() {
        return planService.listAll();
    }

    @GetMapping("/api/admin/plans/{id}")
    public PlanResponse get(@PathVariable Long id) {
        return planService.get(id);
    }

    @PostMapping("/api/admin/plans")
    @ResponseStatus(HttpStatus.CREATED)
    public PlanResponse create(@Valid @RequestBody PlanRequest request) {
        return planService.create(request);
    }

    @PutMapping("/api/admin/plans/{id}")
    public PlanResponse update(@PathVariable Long id, @Valid @RequestBody PlanRequest request) {
        return planService.update(id, request);
    }

    @DeleteMapping("/api/admin/plans/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        planService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
