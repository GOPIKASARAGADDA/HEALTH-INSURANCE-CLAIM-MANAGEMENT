package com.genc.healthinsurance.claim.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.genc.healthinsurance.claim.entity.Claim;
import com.genc.healthinsurance.policy.entity.Policy;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Integer> {
    // Fulfills the requirement to get claims associated with a policyholder (indirectly via policyId)
    List<Claim> findByPolicy(Policy policy);
//    @Query("SELECT c from Claim c where c.policy.policyHolder=:userId")
//    List<Claim> findByPolicyHolderId(@Param("userId")Integer userId);
    List<Claim> findByPolicy_PolicyHolder_UserId(Integer userId);
}