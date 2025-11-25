package com.firstclub.membership.dto;

import com.firstclub.membership.model.MembershipTier;
import com.firstclub.membership.model.PlanType;
import lombok.Data;

@Data
public class SubscribeRequest {
    private String userId;
    private PlanType planType;
    private MembershipTier tier;
}