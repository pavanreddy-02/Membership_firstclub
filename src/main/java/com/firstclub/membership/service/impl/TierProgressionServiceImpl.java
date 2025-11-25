package com.firstclub.membership.service.impl;

import com.firstclub.membership.model.Membership;
import com.firstclub.membership.model.MembershipTier;
import com.firstclub.membership.model.User;
import com.firstclub.membership.repository.MembershipRepository;
import com.firstclub.membership.repository.UserRepository;
import com.firstclub.membership.service.TierProgressionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TierProgressionServiceImpl implements TierProgressionService {

    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;


    // Periodic evaluation: recalc effective tier from points + paid tier
    @Override
    @Transactional
    @Scheduled(fixedRate = 3600000) // every hour
    public void evaluateAndUpgradeTiers() {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            int points = user.getPoints();
            MembershipTier tierFromPoints = determineTier(points);

            MembershipTier paidTier = membershipRepository
                    .findByUserUserIdAndActiveTrue(user.getUserId())
                    .map(Membership::getMembershipTier)
                    .orElse(null);

            MembershipTier effectiveTier =
                    (paidTier != null ? maxTier(tierFromPoints, paidTier) : tierFromPoints);

            user.setMembershipTier(effectiveTier);
            userRepository.save(user);
        }
    }

    // Inactivity penalty
    @Override
    @Transactional
    @Scheduled(cron = "0 0 2 * * *") // daily at 2am
    public void applyInactivityPenalty() {
        List<User> users = userRepository.findAll();
        LocalDate today = LocalDate.now();

        for (User user : users) {
            if (user.getLastOrderDate() == null) {
                continue;
            }

            long daysInactive = ChronoUnit.DAYS.between(user.getLastOrderDate(), today);

            if (daysInactive > 1) {
                // from day 2, -5 logical points/day => -500 stored
                int penalty = (int) ((daysInactive - 1) * 500);
                int newPoints = Math.max(0, user.getPoints() - penalty);
                user.setPoints(newPoints);

                MembershipTier tierFromPoints = determineTier(newPoints);

                MembershipTier paidTier = membershipRepository
                        .findByUserUserIdAndActiveTrue(user.getUserId())
                        .map(Membership::getMembershipTier)
                        .orElse(null);

                MembershipTier effectiveTier =
                        (paidTier != null ? maxTier(tierFromPoints, paidTier) : tierFromPoints);

                user.setMembershipTier(effectiveTier);
                userRepository.save(user);
            }
        }
    }

    //  Apply points on order
    @Override
    @Transactional
    public void applyPointsForOrder(User user, int amountSpent) {

        int earnedFromAmount = amountSpent;
        int orderBonus = 500;
        int firstOrderBonus = (user.getTotalOrders() == 0) ? 5000 : 0;

        int add = earnedFromAmount + orderBonus + firstOrderBonus;

        int newPoints = user.getPoints() + add;
        user.setPoints(newPoints);

        user.setTotalOrders(user.getTotalOrders() + 1);
        user.setTotalSpent(user.getTotalSpent() + amountSpent);
        user.setLastOrderDate(LocalDate.now());

        MembershipTier tierFromPoints = determineTier(newPoints);

        MembershipTier paidTier = membershipRepository
                .findByUserUserIdAndActiveTrue(user.getUserId())
                .map(Membership::getMembershipTier)
                .orElse(null);

        MembershipTier effectiveTier =
                (paidTier != null ? maxTier(tierFromPoints, paidTier) : tierFromPoints);

        user.setMembershipTier(effectiveTier);
        userRepository.save(user);
    }

    private MembershipTier maxTier(MembershipTier a, MembershipTier b) {
        return a.ordinal() >= b.ordinal() ? a : b;
    }

    private MembershipTier determineTier(int points) {
        if (points >= MembershipTier.PLATINUM.getRequiredPoints()) {
            return MembershipTier.PLATINUM;
        } else if (points >= MembershipTier.GOLD.getRequiredPoints()) {
            return MembershipTier.GOLD;
        } else if (points >= MembershipTier.SILVER.getRequiredPoints()) {
            return MembershipTier.SILVER;
        } else {
            return MembershipTier.BRONZE;
        }
    }
}
