package com.firstclub.membership.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstclub.membership.catalog.repository.PlanRepository;
import com.firstclub.membership.catalog.service.PlanService;
import com.firstclub.membership.catalog.service.PolicyService;
import com.firstclub.membership.catalog.service.TierService;
import com.firstclub.membership.catalog.web.dto.PlanRequest;
import com.firstclub.membership.catalog.web.dto.TierRequest;
import com.firstclub.membership.user.service.UserService;
import com.firstclub.membership.user.web.dto.UserRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

/** Seeds default plans/tiers/users from a bundled JSON if the config tables are empty
 *  and {@code membership.seed-on-startup} is true. Goes through the same DTOs as the
 *  admin APIs, so it picks up the same validation. */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final MembershipProperties properties;
    private final ObjectMapper objectMapper;
    private final PlanRepository planRepository;
    private final PlanService planService;
    private final TierService tierService;
    private final UserService userService;
    private final PolicyService policyService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!properties.isSeedOnStartup()) {
            return;
        }
        if (planRepository.count() > 0) {
            log.info("Config already present; skipping seed.");
            return;
        }

        SeedData data = load();
        data.plans().forEach(planService::create);
        data.tiers().forEach(tierService::create);
        data.users().forEach(userService::create);
        policyService.getOrCreate();

        log.info("Seeded {} plans, {} tiers, {} users.",
                data.plans().size(), data.tiers().size(), data.users().size());
    }

    private SeedData load() throws Exception {
        try (InputStream in = new ClassPathResource("default-config.json").getInputStream()) {
            return objectMapper.readValue(in, SeedData.class);
        }
    }

    private record SeedData(List<PlanRequest> plans, List<TierRequest> tiers, List<UserRequest> users) {
    }
}
