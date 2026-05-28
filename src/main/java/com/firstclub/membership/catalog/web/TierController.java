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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TierController {

    private final TierService tierService;

    /** Public: tiers a user can hold, with benefits and qualifying criteria. */
    @GetMapping("/api/tiers")
    public List<TierResponse> listPublic() {
        return tierService.listActive();
    }

    @GetMapping("/api/admin/tiers")
    public List<TierResponse> listAll() {
        return tierService.listAll();
    }

    @GetMapping("/api/admin/tiers/{id}")
    public TierResponse get(@PathVariable Long id) {
        return tierService.get(id);
    }

    @PostMapping("/api/admin/tiers")
    @ResponseStatus(HttpStatus.CREATED)
    public TierResponse create(@Valid @RequestBody TierRequest request) {
        return tierService.create(request);
    }

    @PutMapping("/api/admin/tiers/{id}")
    public TierResponse update(@PathVariable Long id, @Valid @RequestBody TierRequest request) {
        return tierService.update(id, request);
    }

    @DeleteMapping("/api/admin/tiers/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        tierService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
