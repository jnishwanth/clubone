package com.firstclub.membership.catalog.service;

import com.firstclub.membership.catalog.domain.Plan;
import com.firstclub.membership.catalog.repository.PlanRepository;
import com.firstclub.membership.catalog.web.dto.CatalogMapper;
import com.firstclub.membership.catalog.web.dto.PlanRequest;
import com.firstclub.membership.catalog.web.dto.PlanResponse;
import com.firstclub.membership.common.BusinessRuleException;
import com.firstclub.membership.common.Money;
import com.firstclub.membership.common.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;

    @Transactional(readOnly = true)
    public List<PlanResponse> listActive() {
        return planRepository.findByActiveTrueOrderByPriceAsc().stream()
                .map(CatalogMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PlanResponse> listAll() {
        return planRepository.findAll().stream().map(CatalogMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PlanResponse get(Long id) {
        return CatalogMapper.toResponse(require(id));
    }

    @Transactional
    public PlanResponse create(PlanRequest request) {
        if (planRepository.existsByNameIgnoreCase(request.name())) {
            throw new BusinessRuleException("Plan name already exists: " + request.name());
        }
        Plan plan = new Plan();
        apply(plan, request);
        return CatalogMapper.toResponse(planRepository.save(plan));
    }

    @Transactional
    public PlanResponse update(Long id, PlanRequest request) {
        Plan plan = require(id);
        if (planRepository.existsByNameIgnoreCaseAndIdNot(request.name(), id)) {
            throw new BusinessRuleException("Plan name already exists: " + request.name());
        }
        apply(plan, request);
        return CatalogMapper.toResponse(planRepository.save(plan));
    }

    /** Soft-delete: master data referenced by subscriptions is deactivated, not removed. */
    @Transactional
    public void deactivate(Long id) {
        Plan plan = require(id);
        plan.setActive(false);
        planRepository.save(plan);
    }

    /** Internal: resolve an entity for use by other slices within a transaction. */
    @Transactional(readOnly = true)
    public Plan requireById(Long id) {
        return require(id);
    }

    private void apply(Plan plan, PlanRequest request) {
        plan.setName(request.name());
        plan.setBillingPeriod(request.billingPeriod());
        plan.setPrice(Money.of(request.price()));
        plan.setActive(request.active() == null || request.active());
    }

    private Plan require(Long id) {
        return planRepository.findById(id).orElseThrow(() -> NotFoundException.of("Plan", id));
    }
}
