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

/** One qualifying condition for a tier. {@code type} picks the evaluator strategy;
 *  numeric criteria use operator+threshold, COHORT uses stringValue. */
@Entity
@Table(name = "criterion_configs")
@Getter
@Setter
@NoArgsConstructor
public class CriterionConfig extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "tier_id", nullable = false)
    private Tier tier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CriterionType type;

    @Enumerated(EnumType.STRING)
    private ComparisonOperator operator;

    /** Threshold for numeric criteria (ORDER_COUNT, MONTHLY_ORDER_VALUE, REFERRAL_COUNT). */
    private BigDecimal threshold;

    /** Match value for non-numeric criteria (e.g. cohort name). */
    private String stringValue;
}
