package com.firstclub.membership.dto;

import com.firstclub.membership.model.MembershipTier;
import com.firstclub.membership.model.PlanType;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class MembershipResponse {
    private String userId;
    private MembershipTier tier;
    private PlanType plan;
    private LocalDateTime startDate;
    private LocalDateTime expiryDate;
    private boolean active;
    private Map<String, Object> benefits;
    private List<PlanDto> options;
}