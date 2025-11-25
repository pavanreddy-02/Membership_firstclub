package com.firstclub.membership.controller;

import com.firstclub.membership.dto.MembershipResponse;
import com.firstclub.membership.dto.PlanDto;
import com.firstclub.membership.dto.SubscribeRequest;
import com.firstclub.membership.model.Membership;
import com.firstclub.membership.model.MembershipTier;
import com.firstclub.membership.model.Order;
import com.firstclub.membership.service.MembershipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/membership")
public class MembershipController {

    @Autowired
    private MembershipService membershipService;

    @GetMapping("/plans")
    public ResponseEntity<List<PlanDto>> getPlans() {
        return ResponseEntity.ok(membershipService.getPlans());
    }

    @PostMapping("/subscribe")
    public ResponseEntity<Membership> subscribe(@RequestBody SubscribeRequest request) {
        Membership membership = membershipService.subscribe(request);
        return ResponseEntity.ok(membership);
    }

    @GetMapping
    public ResponseEntity<MembershipResponse> getCurrentMembership(@RequestParam String userId) {
        MembershipResponse response = membershipService.getCurrentMembership(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/upgrade")
    public ResponseEntity<MembershipResponse> upgradeTier(
            @RequestParam String userId,
            @RequestParam MembershipTier newTier) {
        MembershipResponse response = membershipService.upgradeTier(userId, newTier);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/downgrade")
    public ResponseEntity<MembershipResponse> downgradeTier(
            @RequestParam String userId,
            @RequestParam MembershipTier newTier) {
        MembershipResponse response = membershipService.downgradeTier(userId, newTier);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/cancel")
    public ResponseEntity<Void> cancelMembership(@RequestParam String userId) {
        membershipService.cancelMembership(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/order")
    public ResponseEntity<Void> recordOrder(@RequestBody Order order) {
        membershipService.recordOrder(order);
        return ResponseEntity.ok().build();
    }
}