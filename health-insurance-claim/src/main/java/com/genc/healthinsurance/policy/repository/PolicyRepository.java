package com.genc.healthinsurance.policy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.genc.healthinsurance.auth.entity.User;
import com.genc.healthinsurance.policy.entity.Policy;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Integer> {
    // Used by ClaimService to fetch policies available for a policyholder to file a claim
    List<Policy> findByPolicyHolder(User policyHolder);
}