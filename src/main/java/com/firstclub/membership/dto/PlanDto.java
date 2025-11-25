package com.firstclub.membership.dto;

import com.firstclub.membership.model.MembershipTier;
import com.firstclub.membership.model.PlanType;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PlanDto {
    private PlanType planType;
    private MembershipTier tier;
    private int price;
    private int durationInDays;
}