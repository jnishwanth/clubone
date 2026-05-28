package com.firstclub.membership.catalog.domain;

import com.firstclub.membership.common.BaseEntity;
import com.firstclub.membership.common.Money;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A subscription plan: billing cadence + base price. */
@Entity
@Table(name = "plans")
@Getter
@Setter
@NoArgsConstructor
public class Plan extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillingPeriod billingPeriod;

    @Column(nullable = false)
    private Money price;

    @Column(nullable = false)
    private boolean active = true;
}
