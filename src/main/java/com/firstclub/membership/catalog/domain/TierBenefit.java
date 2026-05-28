package com.firstclub.membership.catalog.domain;

import com.firstclub.membership.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * A benefit granted by a tier, with a per-tier value (e.g. Gold = 5% off,
 * Platinum = 10%). Value lives on the assignment, not the benefit type, so the
 * same perk can differ across tiers.
 */
@Entity
@Table(name = "tier_benefits")
@Getter
@Setter
@NoArgsConstructor
public class TierBenefit extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "tier_id", nullable = false)
    private Tier tier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BenefitType type;

    /** Numeric value for valued perks (e.g. discount percent); null otherwise. */
    private BigDecimal value;

    private String description;
}
