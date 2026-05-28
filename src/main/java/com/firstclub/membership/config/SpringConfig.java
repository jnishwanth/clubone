package com.firstclub.membership.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MembershipProperties.class)
public class SpringConfig {

    @Bean
    public OpenAPI membershipOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("FirstClub Membership API")
                .version("v1")
                .description("Tiered membership program: plans, configurable tiers/benefits, "
                        + "criteria-driven tier settlement with difference pricing."));
    }
}
