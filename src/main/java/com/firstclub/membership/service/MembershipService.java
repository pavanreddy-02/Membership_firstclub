package com.firstclub.membership.service;

import com.firstclub.membership.dto.MembershipResponse;
import com.firstclub.membership.dto.PlanDto;
import com.firstclub.membership.dto.SubscribeRequest;
import com.firstclub.membership.model.Membership;
import com.firstclub.membership.model.MembershipTier;
import com.firstclub.membership.model.Order;

import java.util.List;

public interface MembershipService {
    List<PlanDto> getPlans();
    Membership subscribe(SubscribeRequest request);
    MembershipResponse getCurrentMembership(String userId);
    MembershipResponse upgradeTier(String userId, MembershipTier newTier);
    MembershipResponse downgradeTier(String userId, MembershipTier newTier);
    void cancelMembership(String userId);
    void recordOrder(Order order);
}