package com.genc.healthinsurance.claim.service;
 
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
 
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
 
    @Autowired
    private ClaimRepository claimRepository;
 
    @Autowired
    private PolicyRepository policyRepository;
 
    @Autowired
    private UserRepository userRepository;
 
    // ---------------- Submit claim (handles both policyholder & agent) ----------------
 // ---------------- Submit claim (handles both policyholder & agent) ----------------
    public Claim processClaimSubmission(Claim claim, Integer userId, String userRole) {
        // Fetch policy
        Policy policy = policyRepository.findById(claim.getPolicy().getPolicyId())
                .orElseThrow(() -> new RuntimeException("Policy not found"));
     
        // Validate claim amount
        if (claim.getClaimAmount() != null && claim.getClaimAmount() > policy.getCoverageAmount()) {
            throw new IllegalArgumentException("Claim amount exceeds policy coverage");
        }
     
        // Validate policy status
        if (policy.getPolicyStatus() != PolicyStatus.ACTIVE) {
            throw new IllegalArgumentException("Cannot submit claim: Policy is not active");
        }
     
        claim.setClaimDate(LocalDate.now());
        claim.setClaimStatus(ClaimStatus.PENDING);
        claim.setPolicy(policy);
     
        // Role-specific logic
        if ("AGENT".equalsIgnoreCase(userRole)) {
            if (claim.getUser() == null) {
                throw new RuntimeException("User ID must be specified for the claim");
            }
     
            // ✅ Step 1: Fetch the user
            User selectedUser = userRepository.findByUserId(claim.getUser().getUserId());
            if (selectedUser == null) {
                throw new RuntimeException("Invalid User ID");
            }
     
            // ✅ Step 2: Check enrollment (many-to-many)
            boolean isEnrolled = policyRepository.existsByUserAndPolicy(selectedUser, policy);
     
            if (!isEnrolled) {
                throw new IllegalArgumentException("This user is not enrolled in the selected policy");
            }
     
            // ✅ Step 3: Save claim
            claim.setUser(selectedUser);
            claim.setAdjuster(null);
            return claimRepository.save(claim);
     
        } else {
            // ✅ Policyholder case
            User currentUser = userRepository.findByUserId(userId);
     
            // Verify that the user is enrolled in the selected policy
            boolean isEnrolled = policyRepository.existsByUserAndPolicy(currentUser, policy);

            if (!isEnrolled) {
                throw new IllegalArgumentException("You are not enrolled in this policy");
            }
     
            claim.setUser(currentUser);
            return claimRepository.save(claim);
        }
    }
     
 
    // ---------------- Existing methods ----------------
    public Claim submitClaim(Claim claim, Integer userId) {
        return claimRepository.save(claim);
    }
 
    public Claim submitClaimByAgent(Claim claim) {
        if (claim.getUser() == null) {
            throw new RuntimeException("User ID must be specified for the claim");
        }
     
        // Step 1: Fetch user
        User selectedUser = userRepository.findByUserId(claim.getUser().getUserId());
        if (selectedUser == null) {
            throw new RuntimeException("Invalid User ID");
        }
     
        // Step 2: Fetch policy
        Policy policy = policyRepository.findById(claim.getPolicy().getPolicyId())
                .orElseThrow(() -> new RuntimeException("Policy not found"));
     
        // Step 3: Validate policy status
        if (policy.getPolicyStatus() != PolicyStatus.ACTIVE) {
            throw new IllegalArgumentException("Cannot submit claim: Policy is not active");
        }
     
        // Step 4: Validate claim amount
        if (claim.getClaimAmount() != null && claim.getClaimAmount() > policy.getCoverageAmount()) {
            throw new IllegalArgumentException("Claim amount exceeds policy coverage");
        }
     
        // Step 5: Check if user is enrolled in policy
        boolean isEnrolled = policyRepository.existsByUserAndPolicy(selectedUser, policy);
        if (!isEnrolled) {
            throw new IllegalArgumentException("This user is not enrolled in the selected policy");
        }
     
        // Step 6: Set defaults
        claim.setUser(selectedUser);
        claim.setPolicy(policy);
        claim.setAdjuster(null);
        claim.setClaimStatus(ClaimStatus.PENDING);
        claim.setClaimDate(LocalDate.now());
     
        // Step 7: Save claim
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
 
    public List<Claim> getAllClaimsByPolicy(Integer policyId) {
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new RuntimeException("Policy not found"));
        return claimRepository.findByPolicy(policy);
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
 