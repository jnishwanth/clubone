package com.firstclub.membership.catalog.service;

import com.firstclub.membership.catalog.domain.CriterionConfig;
import com.firstclub.membership.catalog.domain.CriterionType;
import com.firstclub.membership.catalog.domain.Tier;
import com.firstclub.membership.catalog.domain.TierBenefit;
import com.firstclub.membership.catalog.repository.TierRepository;
import com.firstclub.membership.catalog.web.dto.BenefitDto;
import com.firstclub.membership.catalog.web.dto.CatalogMapper;
import com.firstclub.membership.catalog.web.dto.CriterionDto;
import com.firstclub.membership.catalog.web.dto.TierRequest;
import com.firstclub.membership.catalog.web.dto.TierResponse;
import com.firstclub.membership.common.BusinessRuleException;
import com.firstclub.membership.common.Money;
import com.firstclub.membership.common.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TierService {

    private final TierRepository tierRepository;

    @Transactional(readOnly = true)
    public List<TierResponse> listActive() {
        return tierRepository.findByActiveTrueOrderByRankAsc().stream()
                .map(CatalogMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TierResponse> listAll() {
        return tierRepository.findAllByOrderByRankAsc().stream().map(CatalogMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TierResponse get(Long id) {
        return CatalogMapper.toResponse(require(id));
    }

    @Transactional
    public TierResponse create(TierRequest request) {
        if (tierRepository.existsByNameIgnoreCase(request.name())) {
            throw new BusinessRuleException("Tier name already exists: " + request.name());
        }
        if (tierRepository.existsByRank(request.rank())) {
            throw new BusinessRuleException("Tier rank already in use: " + request.rank());
        }
        Tier tier = new Tier();
        apply(tier, request);
        return CatalogMapper.toResponse(tierRepository.save(tier));
    }

    @Transactional
    public TierResponse update(Long id, TierRequest request) {
        Tier tier = require(id);
        if (tierRepository.existsByNameIgnoreCaseAndIdNot(request.name(), id)) {
            throw new BusinessRuleException("Tier name already exists: " + request.name());
        }
        if (tierRepository.existsByRankAndIdNot(request.rank(), id)) {
            throw new BusinessRuleException("Tier rank already in use: " + request.rank());
        }
        apply(tier, request);
        return CatalogMapper.toResponse(tierRepository.save(tier));
    }

    @Transactional
    public void deactivate(Long id) {
        Tier tier = require(id);
        if (tier.isBaseTier()) {
            throw new BusinessRuleException("The base tier cannot be deactivated.");
        }
        tier.setActive(false);
        tierRepository.save(tier);
    }

    // --- internal cross-slice accessors (entities, used within a transaction) ---

    @Transactional(readOnly = true)
    public List<Tier> activeTiersByRank() {
        return tierRepository.findByActiveTrueOrderByRankAsc();
    }

    @Transactional(readOnly = true)
    public Tier requireById(Long id) {
        return require(id);
    }

    private void apply(Tier tier, TierRequest request) {
        tier.setName(request.name());
        tier.setRank(request.rank());
        tier.setJoiningFee(Money.of(request.joiningFee()));
        tier.setMonthlyFee(Money.of(request.monthlyFee()));
        tier.setActive(request.active() == null || request.active());
        tier.setCriteriaCombinator(request.criteriaCombinator());
        tier.replaceBenefits(toBenefitEntities(request.benefits()));
        tier.replaceCriteria(toCriterionEntities(request.criteria()));
    }

    private List<TierBenefit> toBenefitEntities(List<BenefitDto> dtos) {
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(dto -> {
            TierBenefit benefit = new TierBenefit();
            benefit.setType(dto.type());
            benefit.setValue(dto.value());
            benefit.setDescription(dto.description());
            return benefit;
        }).toList();
    }

    private List<CriterionConfig> toCriterionEntities(List<CriterionDto> dtos) {
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(dto -> {
            validate(dto);
            CriterionConfig criterion = new CriterionConfig();
            criterion.setType(dto.type());
            criterion.setOperator(dto.operator());
            criterion.setThreshold(dto.threshold());
            criterion.setStringValue(dto.stringValue());
            return criterion;
        }).toList();
    }

    private void validate(CriterionDto dto) {
        if (dto.type() == CriterionType.COHORT) {
            if (dto.stringValue() == null || dto.stringValue().isBlank()) {
                throw new BusinessRuleException("COHORT criterion requires a stringValue.");
            }
        } else if (dto.operator() == null || dto.threshold() == null) {
            throw new BusinessRuleException(
                    "Numeric criterion " + dto.type() + " requires an operator and threshold.");
        }
    }

    private Tier require(Long id) {
        return tierRepository.findById(id).orElseThrow(() -> NotFoundException.of("Tier", id));
    }
}
