package com.firstclub.membership.catalog.domain;

import com.firstclub.membership.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Global, runtime-tunable membership policy (Bucket A). Single-row config so the
 * grace window can change live via the admin API without a redeploy.
 */
@Entity
@Table(name = "membership_policy")
@Getter
@Setter
@NoArgsConstructor
public class MembershipPolicy extends BaseEntity {

    /** Days a user has to pay the difference fee before settling down a tier. */
    @Column(nullable = false)
    private int graceWindowDays = 3;
}
