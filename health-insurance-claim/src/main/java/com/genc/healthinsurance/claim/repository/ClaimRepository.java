package com.genc.healthinsurance.claim.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.genc.healthinsurance.auth.entity.User;
import com.genc.healthinsurance.claim.entity.Claim;
import com.genc.healthinsurance.policy.entity.Policy;


@Repository
public interface ClaimRepository extends JpaRepository<Claim, Integer> {
    // Fulfills the requirement to get claims associated with a policyholder (indirectly via policyId)
    List<Claim> findByPolicy(Policy policy);
 // Fetch all claims for a specific user
    List<Claim> findByUser(User user);
    List<Claim> findByAdjuster(User adjusterId);
}