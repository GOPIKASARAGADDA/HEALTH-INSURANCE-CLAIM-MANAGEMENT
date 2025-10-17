package com.genc.healthinsurance.claim.service;
 
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
import com.genc.healthinsurance.auth.entity.User;
import com.genc.healthinsurance.claim.entity.Claim;
import com.genc.healthinsurance.claim.entity.ClaimStatus;
import com.genc.healthinsurance.claim.repository.ClaimRepository;
import com.genc.healthinsurance.policy.entity.Policy;
import com.genc.healthinsurance.policy.repository.PolicyRepository;
import com.genc.healthinsurance.auth.repository.UserRepository;
 
@Service
public class ClaimService {
 
    @Autowired
    private ClaimRepository claimRepository;
 
    @Autowired
    private PolicyRepository policyRepository;
 
    @Autowired
    private UserRepository userRepository;
 
    // ---------------- Submit claim by policyholder ----------------
    public Claim submitClaim(Claim claim,Integer userId) {
        // Fetch policy from DB
    	User currentUser=userRepository.findByUserId(userId);
        Policy policy = policyRepository.findById(claim.getPolicy().getPolicyId())
                .orElseThrow(() -> new RuntimeException("Policy not found"));
        claim.setUser(currentUser);
        claim.setPolicy(policy);
        claim.setClaimDate(LocalDate.now());
        claim.setClaimStatus(ClaimStatus.PENDING);
 
        return claimRepository.save(claim);
    }
 
    // ---------------- Submit claim manually by agent ----------------
    public Claim submitClaimByAgent(Claim claim) {
        // Agent manually provides all fields
        Policy policy = policyRepository.findById(claim.getPolicy().getPolicyId())
                .orElseThrow(() -> new RuntimeException("Policy not found"));
        
        claim.setPolicy(policy);
        claim.setClaimDate(LocalDate.now());
        claim.setClaimStatus(ClaimStatus.PENDING);
claim.setAdjuster(null); 
if(claim.getUser()==null) {
	throw new RuntimeException("userid must be specified for the claim");
}
        return claimRepository.save(claim);
    }
 
    // ---------------- Get claim details by claimId ----------------
    public Optional<Claim> getClaimDetails(Integer claimId) {
        return claimRepository.findById(claimId);
    }
 
    // ---------------- Update claim status ----------------
    public Claim updateClaimStatus(Integer claimId, ClaimStatus status) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found"));
        claim.setClaimStatus(status);
        return claimRepository.save(claim);
    }
 
    // ---------------- Get all claims by policy ----------------
    public List<Claim> getAllClaimsByPolicy(Integer policyId) {
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new RuntimeException("Policy not found"));
        return claimRepository.findByPolicy(policy);
    }
 
    // ---------------- Get claims by policyholder userId ----------------
    public List<Claim> getClaimsByUserId(Integer userId) {
    	User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
  return claimRepository.findByUser(user);
    }
 
    // ---------------- Get claims submitted by(adjusterId) ----------------
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
    public List<Policy> getAllPolicies() {
        return policyRepository.findAll();
    }
 
    public List<Policy> getPoliciesByUser(User user) {
        return policyRepository.findByEnrolledUsersContaining(user);
    }
}
 