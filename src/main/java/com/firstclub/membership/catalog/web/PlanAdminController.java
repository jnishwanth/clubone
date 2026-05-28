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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Admin CRUD for plans. */
@RestController
@RequestMapping("/api/admin/plans")
@RequiredArgsConstructor
public class PlanAdminController {

    private final PlanService planService;

    @GetMapping
    public List<PlanResponse> listAll() {
        return planService.listAll();
    }

    @GetMapping("/{id}")
    public PlanResponse get(@PathVariable Long id) {
        return planService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlanResponse create(@Valid @RequestBody PlanRequest request) {
        return planService.create(request);
    }

    @PutMapping("/{id}")
    public PlanResponse update(@PathVariable Long id, @Valid @RequestBody PlanRequest request) {
        return planService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        planService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
