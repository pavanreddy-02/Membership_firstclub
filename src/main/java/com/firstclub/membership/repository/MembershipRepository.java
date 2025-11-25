package com.firstclub.membership.repository;

import com.firstclub.membership.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {
    Optional<Membership> findByUserUserIdAndActiveTrue(String userId);
}