package com.genc.healthinsurance.claim.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.genc.healthinsurance.auth.entity.User;
import com.genc.healthinsurance.claim.entity.Claim;
import com.genc.healthinsurance.policy.entity.Policy;


@Repository
public interface ClaimRepository extends JpaRepository<Claim, Integer> {
    List<Claim> findByPolicy(Policy policyId);
 // Fetch all claims for a specific user
    List<Claim> findByUser(User userId);
    List<Claim> findByAdjuster(User adjusterId);
}