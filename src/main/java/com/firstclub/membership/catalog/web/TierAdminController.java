package com.firstclub.membership.catalog.web;

import com.firstclub.membership.catalog.service.TierService;
import com.firstclub.membership.catalog.web.dto.TierRequest;
import com.firstclub.membership.catalog.web.dto.TierResponse;
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

/** Admin CRUD for tiers, their benefits, and their qualifying criteria. */
@RestController
@RequestMapping("/api/admin/tiers")
@RequiredArgsConstructor
public class TierAdminController {

    private final TierService tierService;

    @GetMapping
    public List<TierResponse> listAll() {
        return tierService.listAll();
    }

    @GetMapping("/{id}")
    public TierResponse get(@PathVariable Long id) {
        return tierService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TierResponse create(@Valid @RequestBody TierRequest request) {
        return tierService.create(request);
    }

    @PutMapping("/{id}")
    public TierResponse update(@PathVariable Long id, @Valid @RequestBody TierRequest request) {
        return tierService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        tierService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
