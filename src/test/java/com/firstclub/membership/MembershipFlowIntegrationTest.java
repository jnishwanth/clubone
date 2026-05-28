package com.firstclub.membership;

import com.firstclub.membership.benefit.web.dto.BenefitPreviewResponse;
import com.firstclub.membership.benefit.web.dto.CartPreviewRequest;
import com.firstclub.membership.catalog.web.dto.PlanResponse;
import com.firstclub.membership.catalog.web.dto.TierResponse;
import com.firstclub.membership.membership.web.dto.MembershipView;
import com.firstclub.membership.membership.web.dto.SettlementRunResponse;
import com.firstclub.membership.membership.web.dto.SubscribeRequest;
import com.firstclub.membership.user.web.dto.UserRequest;
import com.firstclub.membership.user.web.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/** End-to-end happy path over the real HTTP stack against the seeded catalog. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MembershipFlowIntegrationTest {

    @Autowired
    private TestRestTemplate rest;

    @Test
    void subscribeAccrueAndSettleKeepsTierWhenCriteriaMet() {
        long goldId = tierIdByName("Gold");
        long yearlyId = planIdByName("Yearly");

        UserResponse user = rest.postForObject("/api/users",
                new UserRequest("Flow Test", null), UserResponse.class);
        assertThat(user.id()).isNotNull();

        ResponseEntity<MembershipView> subscribed = rest.postForEntity(
                "/api/users/" + user.id() + "/subscription",
                new SubscribeRequest(yearlyId, goldId), MembershipView.class);
        assertThat(subscribed.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(subscribed.getBody().tierName()).isEqualTo("Gold");

        // Five orders → meets Gold's ORDER_COUNT >= 5.
        for (int i = 0; i < 5; i++) {
            rest.postForEntity("/api/orders/events",
                    new OrderEvent(user.id(), BigDecimal.valueOf(1000), null), Void.class);
        }

        SettlementRunResponse run = rest.postForObject("/api/admin/tiering/run", null, SettlementRunResponse.class);
        assertThat(run.subscriptionsProcessed()).isGreaterThanOrEqualTo(1);

        MembershipView after = rest.getForObject("/api/users/" + user.id() + "/membership", MembershipView.class);
        assertThat(after.tierName()).isEqualTo("Gold"); // waived, retained

        BenefitPreviewResponse preview = rest.postForObject(
                "/api/users/" + user.id() + "/benefits/preview",
                new CartPreviewRequest(BigDecimal.valueOf(2000), BigDecimal.valueOf(50)),
                BenefitPreviewResponse.class);
        assertThat(preview.deliveryWaived()).isTrue();
        assertThat(preview.payableTotal()).isEqualByComparingTo("1900.00"); // 2000 - 5% - waived delivery
    }

    private long tierIdByName(String name) {
        TierResponse[] tiers = rest.getForObject("/api/tiers", TierResponse[].class);
        return Arrays.stream(tiers).filter(t -> t.name().equals(name)).findFirst().orElseThrow().id();
    }

    private long planIdByName(String name) {
        PlanResponse[] plans = rest.getForObject("/api/plans", PlanResponse[].class);
        return Arrays.stream(plans).filter(p -> p.name().equals(name)).findFirst().orElseThrow().id();
    }

    /** Local payload mirroring OrderEventRequest (whose fields are validated server-side). */
    private record OrderEvent(Long userId, BigDecimal amount, String period) {
    }
}
