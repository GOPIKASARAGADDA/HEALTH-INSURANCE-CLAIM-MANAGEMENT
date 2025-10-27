package com.genc.healthinsurance.claim.service;
 
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.genc.healthinsurance.auth.entity.User;
import com.genc.healthinsurance.auth.repository.UserRepository;
import com.genc.healthinsurance.claim.entity.Claim;
import com.genc.healthinsurance.claim.entity.ClaimStatus;
import com.genc.healthinsurance.claim.repository.ClaimRepository;
import com.genc.healthinsurance.policy.entity.Policy;
import com.genc.healthinsurance.policy.entity.PolicyStatus;
import com.genc.healthinsurance.policy.repository.PolicyRepository;
 
@Service
public class ClaimService {
	private static final Logger logger=LoggerFactory.getLogger(ClaimService.class);

    @Autowired
    private ClaimRepository claimRepository;
 
    @Autowired
    private PolicyRepository policyRepository;
 
    @Autowired
    private UserRepository userRepository;
 
 // ---------------- Submit claim (handles both policyholder & agent) ----------------
    public Claim processClaimSubmission(Claim claim, Integer userId, String userRole) {
    	
    	logger.debug("processing claim submission for user {}",userId);
        // Step 1: Fetch and validate policy
        Policy policy = policyRepository.findById(claim.getPolicy().getPolicyId())
                .orElseThrow(() -> new RuntimeException("Policy not found"));

        if (policy.getPolicyStatus() != PolicyStatus.ACTIVE) {
            throw new IllegalArgumentException("Cannot submit claim: Policy is not active");
        }

        if (claim.getClaimAmount() != null && claim.getClaimAmount() > policy.getCoverageAmount()) {
            throw new IllegalArgumentException("Claim amount exceeds policy coverage");
        }

        // Step 2: Determine user based on role
        User selectedUser;
        if ("AGENT".equalsIgnoreCase(userRole)) {
            if (claim.getUser() == null || claim.getUser().getUserId() == 0) {
                throw new RuntimeException("User ID must be specified for the claim");
            }
            selectedUser = userRepository.findByUserId(claim.getUser().getUserId());
            if (selectedUser == null) {
                throw new RuntimeException("Invalid User ID");
            }
        } 
        
        //policyholder
        else {
            selectedUser = userRepository.findByUserId(userId);
        }

        // Step 3: Check enrollment common
        boolean isEnrolled = policyRepository.existsByUserAndPolicy(selectedUser, policy);
        if (!isEnrolled) {
            throw new IllegalArgumentException("User is not enrolled in the selected policy");
        }

        // Step 4: Set claim defaults
        claim.setUser(selectedUser);
        claim.setPolicy(policy);
        claim.setAdjuster(null);
        claim.setClaimStatus(ClaimStatus.PENDING);
        claim.setClaimDate(LocalDate.now());

        // Step 5: Save and return
        return claimRepository.save(claim);
    }
  
 
    
    public Optional<Claim> getClaimDetails(Integer claimId) {
        return claimRepository.findById(claimId);
    }
 
    public Claim updateClaimStatus(Integer claimId, ClaimStatus status) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found"));
        claim.setClaimStatus(status);
        return claimRepository.save(claim);
    }
 
    public List<Claim> getClaimsByUserId(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return claimRepository.findByUser(user);
    }
 
    public List<Claim> getClaimsByAdjuster(Integer adjusterId) {
        User adjuster = userRepository.findById(adjusterId)
                .orElseThrow(() -> new RuntimeException("Adjuster not found"));
        return claimRepository.findByAdjuster(adjuster);
    }
 
    public Claim assignAdjuster(Integer claimId, Integer adjusterId) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found"));
        User adjuster = userRepository.findById(adjusterId)
                .orElseThrow(() -> new RuntimeException("Adjuster not found"));
        claim.setAdjuster(adjuster);
        return claimRepository.save(claim);
    }
 
    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }
 
    public List<Claim> getClaimsForUserByPolicy(Integer userId, Integer policyId) {
        List<Claim> claims = getClaimsByUserId(userId);
        if (policyId != null) {
            claims = claims.stream()
                    .filter(c -> c.getPolicy() != null && c.getPolicy().getPolicyId().equals(policyId))
                    .collect(Collectors.toList());
        }
        return claims;
    }
}
 