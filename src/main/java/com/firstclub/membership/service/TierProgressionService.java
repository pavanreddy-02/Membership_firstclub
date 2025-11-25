package com.firstclub.membership.service;

import com.firstclub.membership.model.User;

public interface TierProgressionService {
    void evaluateAndUpgradeTiers();

    void applyInactivityPenalty();

    void applyPointsForOrder(User user, int amountSpent);
}