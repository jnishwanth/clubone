package com.firstclub.membership.user.domain;

import com.firstclub.membership.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Minimal user. No auth — identity isn't what's being evaluated. {@code cohort}
 * feeds the COHORT tier-qualification criterion.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {

    @Column(nullable = false)
    private String name;

    /** Optional marketing/segment cohort (e.g. "VIP"); used by cohort criteria. */
    private String cohort;
}
