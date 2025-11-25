package com.firstclub.membership.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    private String userId;

    private String email;

    private int points;

    private int totalOrders;

    private int totalSpent;

    private LocalDate lastOrderDate;

    @Enumerated(EnumType.STRING)
    private MembershipTier membershipTier;

    @PrePersist
    public void initDefaults() {
        if (points < 0) points = 0;
        if (totalOrders < 0) totalOrders = 0;
        if (totalSpent < 0) totalSpent = 0;

        if (lastOrderDate == null) lastOrderDate = LocalDate.now();
        if (membershipTier == null) membershipTier = MembershipTier.BRONZE;
    }
}