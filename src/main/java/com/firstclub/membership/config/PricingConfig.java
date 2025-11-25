package com.firstclub.membership.config;

import com.firstclub.membership.model.MembershipTier;
import com.firstclub.membership.model.PlanType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PricingConfig {

    private final Environment env;

    public int getPrice(PlanType plan, MembershipTier tier) {
        String key = String.format("pricing.%s.%s", plan.name(), tier.name());
        return Integer.parseInt(env.getProperty(key, "0"));
    }
}

