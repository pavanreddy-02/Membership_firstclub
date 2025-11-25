package com.firstclub.membership.model;

public enum MembershipTier {
    BRONZE(0),
    SILVER(10000),
    GOLD(30000),
    PLATINUM(50000);

    private final int requiredPoints;

    MembershipTier(int requiredPoints) {
        this.requiredPoints = requiredPoints;
    }

    public int getRequiredPoints() {
        return requiredPoints;
    }
}