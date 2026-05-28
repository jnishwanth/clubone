package com.firstclub.membership.catalog.web;

import com.firstclub.membership.catalog.service.TierService;
import com.firstclub.membership.catalog.web.dto.TierResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Public-facing: tiers a user can hold, with benefits and qualifying criteria.
 *  Admin CRUD lives in {@link TierAdminController}. */
@RestController
@RequiredArgsConstructor
public class TierController {

    private final TierService tierService;

    @GetMapping("/api/tiers")
    public List<TierResponse> listPublic() {
        return tierService.listActive();
    }
}
