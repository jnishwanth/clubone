package com.firstclub.membership.catalog.domain;

import com.firstclub.membership.common.BaseEntity;
import com.firstclub.membership.common.Money;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

/** A membership tier. Owns its benefits and qualifying criteria via cascade. rank orders
 *  tiers (0 = base / free), and difference pricing compares ranks + monthly fees. */
@Entity
@Table(name = "tiers")
@Getter
@Setter
@NoArgsConstructor
public class Tier extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    /** 0 = base tier (free with any subscription); higher = more premium. */
    @Column(nullable = false, unique = true)
    private int rank;

    @Column(nullable = false)
    private Money joiningFee = Money.ZERO;

    @Column(nullable = false)
    private Money monthlyFee = Money.ZERO;

    @Column(nullable = false)
    private boolean active = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CriteriaCombinator criteriaCombinator = CriteriaCombinator.ALL;

    @OneToMany(mappedBy = "tier", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 32)
    private List<TierBenefit> benefits = new ArrayList<>();

    @OneToMany(mappedBy = "tier", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 32)
    private List<CriterionConfig> qualifyingCriteria = new ArrayList<>();

    public boolean isBaseTier() {
        return rank == 0;
    }

    public void addBenefit(TierBenefit benefit) {
        benefit.setTier(this);
        benefits.add(benefit);
    }

    public void addCriterion(CriterionConfig criterion) {
        criterion.setTier(this);
        qualifyingCriteria.add(criterion);
    }

    public void replaceBenefits(List<TierBenefit> newBenefits) {
        benefits.clear();
        newBenefits.forEach(this::addBenefit);
    }

    public void replaceCriteria(List<CriterionConfig> newCriteria) {
        qualifyingCriteria.clear();
        newCriteria.forEach(this::addCriterion);
    }
}
