package com.genc.healthinsurance.claim.service;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
 
import com.genc.healthinsurance.claim.entity.Claim;
import com.genc.healthinsurance.claim.entity.ClaimStatus;
import com.genc.healthinsurance.claim.repository.ClaimRepository;
import com.genc.healthinsurance.policy.repository.PolicyRepository;
import com.genc.healthinsurance.policy.entity.Policy;
import com.genc.healthinsurance.auth.entity.User;
import com.genc.healthinsurance.auth.repository.UserRepository;
 
@Service
public class ClaimService {
 
    @Autowired
    private ClaimRepository claimRepository;
 
    @Autowired
    private PolicyRepository policyRepository;
 
    @Autowired
    private UserRepository userRepository;
 
    // Submit a new claim
    public Claim submitClaim(Claim claimData) {
        Policy policy = policyRepository.findById(claimData.getPolicy().getPolicyId())
                .orElseThrow(() -> new RuntimeException("Policy not found"));
        claimData.setPolicy(policy);
        claimData.setClaimDate(LocalDate.now());
        claimData.setClaimStatus(ClaimStatus.PENDING);
 
        if (claimData.getAdjuster() != null) {
            User adjuster = userRepository.findById(claimData.getAdjuster().getUserId())
                    .orElseThrow(() -> new RuntimeException("Adjuster not found"));
            claimData.setAdjuster(adjuster);
        }
 
        return claimRepository.save(claimData);
    }
 
    // Get single claim
    public Optional<Claim> getClaimDetails(Integer claimId) {
        return claimRepository.findById(claimId);
    }
 
    // Update claim status
    public Claim updateClaimStatus(Integer claimId, ClaimStatus status) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found"));
        claim.setClaimStatus(status);
        return claimRepository.save(claim);
    }
 
    // Get all claims by policy
    public List<Claim> getAllClaimsByPolicy(Integer policyId) {
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new RuntimeException("Policy not found"));
        return claimRepository.findByPolicy(policy);
    }
    //get all claims by uid
    public List<Claim> getClaimsByUserId(Integer userId){
    	return claimRepository.findByPolicy_PolicyHolder_UserId(userId);
    }
}
 