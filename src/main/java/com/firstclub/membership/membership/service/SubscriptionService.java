package com.firstclub.membership.membership.service;

import com.firstclub.membership.activity.domain.ActivitySnapshot;
import com.firstclub.membership.activity.service.ActivityService;
import com.firstclub.membership.catalog.domain.Plan;
import com.firstclub.membership.catalog.domain.Tier;
import com.firstclub.membership.catalog.service.PlanService;
import com.firstclub.membership.catalog.service.TierService;
import com.firstclub.membership.catalog.web.dto.CatalogMapper;
import com.firstclub.membership.common.BusinessRuleException;
import com.firstclub.membership.common.NotFoundException;
import com.firstclub.membership.membership.domain.Subscription;
import com.firstclub.membership.membership.domain.SubscriptionStatus;
import com.firstclub.membership.membership.domain.SubscriptionTierTransitions;
import com.firstclub.membership.membership.repository.SubscriptionRepository;
import com.firstclub.membership.membership.web.dto.MembershipView;
import com.firstclub.membership.membership.web.dto.SubscribeRequest;
import com.firstclub.membership.payment.domain.PaymentPurpose;
import com.firstclub.membership.payment.service.PaymentService;
import com.firstclub.membership.tiering.criteria.EvaluationContext;
import com.firstclub.membership.tiering.progress.TierProgressService;
import com.firstclub.membership.tiering.progress.TierProgressView;
import com.firstclub.membership.user.domain.User;
import com.firstclub.membership.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/** Use-case API for membership: subscribe / upgrade / downgrade / cancel / view. */
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserService userService;
    private final PlanService planService;
    private final TierService tierService;
    private final PaymentService paymentService;
    private final ActivityService activityService;
    private final TierProgressService tierProgressService;

    @Transactional
    public MembershipView subscribe(Long userId, SubscribeRequest request) {
        userService.requireById(userId);
        if (subscriptionRepository.existsByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)) {
            throw new BusinessRuleException("User already has an active subscription.");
        }
        Plan plan = planService.requireById(request.planId());
        if (!plan.isActive()) {
            throw new BusinessRuleException("Plan is not active: " + plan.getName());
        }
        Tier tier = tierService.requireById(request.tierId());
        if (!tier.isActive()) {
            throw new BusinessRuleException("Tier is not active: " + tier.getName());
        }

        Subscription subscription = new Subscription();
        subscription.setUserId(userId);
        subscription.setPlanId(plan.getId());
        subscription.setHeldTierId(tier.getId());
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStartDate(LocalDate.now());
        subscription.setExpiryDate(LocalDate.now().plusMonths(plan.getBillingPeriod().months()));
        subscription = subscriptionRepository.save(subscription);

        if (!plan.getPrice().isZero()) {
            paymentService.chargeNow(userId, subscription.getId(), plan.getPrice(), PaymentPurpose.SUBSCRIPTION);
        }
        // Entering a higher tier upfront costs its joining fee.
        if (!tier.isBaseTier() && !tier.getJoiningFee().isZero()) {
            paymentService.chargeNow(userId, subscription.getId(), tier.getJoiningFee(), PaymentPurpose.TIER_JOINING);
        }
        return view(subscription);
    }

    @Transactional
    public MembershipView upgrade(Long subscriptionId, Long targetTierId) {
        Subscription subscription = requireActive(subscriptionId);
        Tier current = tierService.requireById(subscription.getHeldTierId());
        Tier target = tierService.requireById(targetTierId);
        if (!target.isActive()) {
            throw new BusinessRuleException("Target tier is not active: " + target.getName());
        }
        SubscriptionTierTransitions.validateUpgrade(current, target);
        if (!target.getJoiningFee().isZero()) {
            paymentService.chargeNow(subscription.getUserId(), subscription.getId(),
                    target.getJoiningFee(), PaymentPurpose.TIER_JOINING);
        }
        subscription.setHeldTierId(target.getId());
        subscriptionRepository.save(subscription);
        return view(subscription);
    }

    @Transactional
    public MembershipView downgrade(Long subscriptionId, Long targetTierId) {
        Subscription subscription = requireActive(subscriptionId);
        Tier current = tierService.requireById(subscription.getHeldTierId());
        Tier target = tierService.requireById(targetTierId);
        SubscriptionTierTransitions.validateDowngrade(current, target);
        subscription.setHeldTierId(target.getId());
        subscriptionRepository.save(subscription);
        return view(subscription);
    }

    @Transactional
    public MembershipView cancel(Long subscriptionId) {
        Subscription subscription = requireActive(subscriptionId);
        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscriptionRepository.save(subscription);
        return view(subscription);
    }

    @Transactional(readOnly = true)
    public MembershipView getMembership(Long userId) {
        Subscription subscription = subscriptionRepository
                .findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("No active membership for user " + userId));
        return view(subscription);
    }

    /** Internal: the tier a user currently holds on their active subscription. */
    @Transactional(readOnly = true)
    public Long activeHeldTierId(Long userId) {
        return subscriptionRepository.findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .map(Subscription::getHeldTierId)
                .orElseThrow(() -> new NotFoundException("No active membership for user " + userId));
    }

    /**
     * Expire every active subscription whose period has elapsed. Set-based and
     * idempotent (see {@link SubscriptionRepository#expireActiveBefore}). Returns
     * the number expired.
     */
    @Transactional
    public int expireDueSubscriptions() {
        return subscriptionRepository.expireActiveBefore(LocalDate.now(), Instant.now());
    }

    private MembershipView view(Subscription subscription) {
        Plan plan = planService.requireById(subscription.getPlanId());
        Tier held = tierService.requireById(subscription.getHeldTierId());
        User user = userService.requireById(subscription.getUserId());

        ActivitySnapshot snapshot = activityService.snapshot(subscription.getUserId(), YearMonth.now());
        EvaluationContext context = new EvaluationContext(user.getCohort(), snapshot);
        List<Tier> activeTiers = tierService.activeTiersByRank();
        TierProgressView progress = tierProgressService.nextTierProgress(activeTiers, context, held.getRank());

        return new MembershipView(
                subscription.getId(),
                subscription.getUserId(),
                plan.getName(),
                plan.getBillingPeriod(),
                subscription.getStatus(),
                subscription.getStartDate(),
                subscription.getExpiryDate(),
                held.getName(),
                held.getRank(),
                CatalogMapper.toResponse(held).benefits(),
                progress);
    }

    private Subscription requireActive(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> NotFoundException.of("Subscription", subscriptionId));
        if (!subscription.isActive()) {
            throw new BusinessRuleException("Subscription is not active: " + subscriptionId);
        }
        return subscription;
    }
}
