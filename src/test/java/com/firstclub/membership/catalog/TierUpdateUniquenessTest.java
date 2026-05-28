package com.firstclub.membership.catalog;

import com.firstclub.membership.catalog.domain.CriteriaCombinator;
import com.firstclub.membership.catalog.repository.TierRepository;
import com.firstclub.membership.catalog.service.TierService;
import com.firstclub.membership.catalog.web.dto.TierRequest;
import com.firstclub.membership.catalog.web.dto.TierResponse;
import com.firstclub.membership.common.BusinessRuleException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Updating a tier must not collide with another tier's name or rank. */
@SpringBootTest
class TierUpdateUniquenessTest {

    @Autowired
    private TierService tierService;

    @Autowired
    private TierRepository tierRepository;

    @Test
    void rejectsRenamingOntoAnotherTiersName() {
        long goldId = idOf("Gold");
        // Gold's own rank (1), but Silver's name.
        TierRequest dupName = tierRequest("Silver", 1);

        assertThatThrownBy(() -> tierService.update(goldId, dupName))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void rejectsTakingAnotherTiersRank() {
        long goldId = idOf("Gold");
        // Gold's own name, but Silver's rank (0).
        TierRequest dupRank = tierRequest("Gold", 0);

        assertThatThrownBy(() -> tierService.update(goldId, dupRank))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void allowsUpdatingATierKeepingItsOwnNameAndRank() {
        // Fresh tier so we don't mutate the seeded catalog.
        TierResponse created = tierService.create(tierRequest("Diamond", 99));

        TierResponse updated = tierService.update(created.id(),
                new TierRequest("Diamond", 99, BigDecimal.valueOf(3499), BigDecimal.valueOf(999),
                        true, CriteriaCombinator.ANY, List.of(), List.of()));

        assertThat(updated.joiningFee()).isEqualByComparingTo("3499.00");
    }

    private long idOf(String name) {
        return tierRepository.findByNameIgnoreCase(name).orElseThrow().getId();
    }

    private TierRequest tierRequest(String name, int rank) {
        return new TierRequest(name, rank, BigDecimal.valueOf(499), BigDecimal.valueOf(199),
                true, CriteriaCombinator.ANY, List.of(), List.of());
    }
}
