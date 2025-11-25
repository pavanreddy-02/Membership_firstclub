package com.firstclub.membership.service.impl;

import com.firstclub.membership.dto.MembershipResponse;
import com.firstclub.membership.dto.PlanDto;
import com.firstclub.membership.dto.SubscribeRequest;
import com.firstclub.membership.model.*;
import com.firstclub.membership.repository.MembershipPlanRepository;
import com.firstclub.membership.repository.MembershipRepository;
import com.firstclub.membership.repository.OrderRepository;
import com.firstclub.membership.repository.UserRepository;
import com.firstclub.membership.service.MembershipService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class MembershipServiceImpl implements MembershipService {

    @Autowired
    private MembershipPlanRepository membershipPlanRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    @Transactional
    public List<PlanDto> getPlans() {

        List<PlanDto> result = new ArrayList<>();

        List<MembershipPlan> plans = membershipPlanRepository.findAll();

        for (MembershipPlan plan : plans) {
            PlanType planType = plan.getPlanType();

            for (MembershipTier tier : MembershipTier.values()) {

                PlanDto dto = new PlanDto();
                dto.setPlanType(planType);
                dto.setTier(tier);
                dto.setDurationInDays(plan.getDurationInDays());
                dto.setPrice(resolvePrice(planType, tier));

                result.add(dto);
            }
        }

        return result;
    }

    private int resolvePrice(PlanType planType, MembershipTier tier) {
        return switch (planType) {
            case MONTHLY -> switch (tier) {
                case BRONZE -> 0;
                case SILVER -> 99;
                case GOLD -> 199;
                case PLATINUM -> 299;
            };
            case QUARTERLY -> switch (tier) {
                case BRONZE -> 0;
                case SILVER -> 249;
                case GOLD -> 399;
                case PLATINUM -> 599;
            };
            case YEARLY -> switch (tier) {
                case BRONZE -> 0;
                case SILVER -> 799;
                case GOLD -> 1199;
                case PLATINUM -> 1499;
            };
        };
    }

    @Override
    @Transactional
    public Membership subscribe(SubscribeRequest request) {

        // Get or create user
        User user = userRepository.findById(request.getUserId())
                .orElseGet(() -> {
                    User u = new User();
                    u.setUserId(request.getUserId());
                    u.setPoints(0);
                    u.setTotalOrders(0);
                    u.setTotalSpent(0);
                    u.setMembershipTier(MembershipTier.BRONZE);
                    return userRepository.save(u);
                });

        MembershipPlan plan = membershipPlanRepository.findByPlanType(request.getPlanType())
                .orElseThrow(() -> new IllegalArgumentException("Plan not found: " + request.getPlanType()));

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime expiry = start.plusDays(plan.getDurationInDays());

        Membership membership = new Membership();
        membership.setUser(user);
        membership.setPlan(plan);
        membership.setMembershipTier(request.getTier()); // paid tier
        membership.setStartDate(start);
        membership.setExpiryDate(expiry);
        membership.setActive(true);

        Membership saved = membershipRepository.save(membership);

        // Immediately reflect paid tier as effective
        user.setMembershipTier(request.getTier());
        userRepository.save(user);

        return saved;
    }

    @Override
    @Transactional
    public MembershipResponse getCurrentMembership(String userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        MembershipResponse response = new MembershipResponse();
        response.setUserId(user.getUserId());
        response.setTier(user.getMembershipTier());

        membershipRepository.findByUserUserIdAndActiveTrue(userId)
                .ifPresentOrElse(membership -> {
                    response.setPlan(membership.getPlan().getPlanType());
                    response.setStartDate(membership.getStartDate());
                    response.setExpiryDate(membership.getExpiryDate());
                    response.setActive(true);
                    response.setBenefits(calculateBenefits(membership.getMembershipTier()));
                }, () -> {
                    response.setActive(false);
                    response.setBenefits(Collections.emptyMap());
                });

        return response;
    }

    @Override
    @Transactional
    public MembershipResponse upgradeTier(String userId, MembershipTier newTier) {

        Membership membership = membershipRepository.findByUserUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new IllegalArgumentException("No active membership for user: " + userId));

        if (newTier.ordinal() <= membership.getMembershipTier().ordinal()) {
            throw new IllegalArgumentException("New tier must be higher than current tier");
        }

        membership.setMembershipTier(newTier);
        membershipRepository.save(membership);

        // update user effective tier too
        User user = membership.getUser();
        user.setMembershipTier(newTier);
        userRepository.save(user);

        return getCurrentMembership(userId);
    }

    @Override
    @Transactional
    public MembershipResponse downgradeTier(String userId, MembershipTier newTier) {

        Membership membership = membershipRepository.findByUserUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new IllegalArgumentException("No active membership for user: " + userId));

        if (newTier.ordinal() >= membership.getMembershipTier().ordinal()) {
            throw new IllegalArgumentException("New tier must be lower than current tier");
        }

        membership.setMembershipTier(newTier);
        membershipRepository.save(membership);

        User user = membership.getUser();
        user.setMembershipTier(newTier);
        userRepository.save(user);

        return getCurrentMembership(userId);
    }

    @Override
    @Transactional
    public void cancelMembership(String userId) {

        membershipRepository.findByUserUserIdAndActiveTrue(userId)
                .ifPresent(membership -> {
                    membership.setActive(false);
                    membership.setExpiryDate(LocalDateTime.now());
                    membershipRepository.save(membership);
                });

        // After cancel, user tier should fall back to tier from points;
        // TierProgressionService will recalculate based on points later.
    }

    // ========= 7. RECORD ORDER (hook to tier progression) =========

    @Override
    @Transactional
    public void recordOrder(Order order) {
        // Just persist the order here; tier progression is handled in tier progression service
        orderRepository.save(order);
    }

    private Map<String, Object> calculateBenefits(MembershipTier tier) {
        Map<String, Object> benefits = new HashMap<>();

        switch (tier) {
            case BRONZE:
                // no benefits
                break;
            case SILVER:
                benefits.put("freeDelivery", true);
                benefits.put("discountPercent", 5);
                benefits.put("earlyAccess", false);
                break;
            case GOLD:
                benefits.put("freeDelivery", true);
                benefits.put("expressDelivery", true);
                benefits.put("discountPercent", 10);
                benefits.put("earlyAccess", true);
                break;
            case PLATINUM:
                benefits.put("freeDelivery", true);
                benefits.put("expressDelivery", true);
                benefits.put("discountPercent", 15);
                benefits.put("earlyAccess", true);
                benefits.put("prioritySupport", true);
                benefits.put("exclusiveDeals", true);
                break;
        }

        return benefits;
    }
}