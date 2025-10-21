package com.genc.healthinsurancetest.claims;
 
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.genc.healthinsurance.auth.entity.User;
import com.genc.healthinsurance.auth.repository.UserRepository;
import com.genc.healthinsurance.claim.entity.Claim;
import com.genc.healthinsurance.claim.entity.ClaimStatus;
import com.genc.healthinsurance.claim.repository.ClaimRepository;
import com.genc.healthinsurance.claim.service.ClaimService;
import com.genc.healthinsurance.policy.entity.Policy;
import com.genc.healthinsurance.policy.entity.PolicyStatus;
import com.genc.healthinsurance.policy.repository.PolicyRepository;
 
public class ClaimServiceTest {
 
    @Mock
    private ClaimRepository claimRepository;
 
    @Mock
    private PolicyRepository policyRepository;
 
    @Mock
    private UserRepository userRepository;
 
    @InjectMocks
    private ClaimService claimService;
 
    private User user;
    private Policy policy;
    private Claim claim;
 
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
 
        user = new User();
        user.setUserId(1);
 
        policy = new Policy();
        policy.setPolicyId(100);
        policy.setCoverageAmount(10000.0);
        policy.setPolicyStatus(PolicyStatus.ACTIVE);
 
        claim = new Claim();
        claim.setClaimAmount(5000.0);
        claim.setPolicy(policy);
    }
 
    @Test
    void testSubmitClaim_Success() {
        when(userRepository.findByUserId(1)).thenReturn(user);
        when(policyRepository.findById(100)).thenReturn(Optional.of(policy));
        //when(claimRepository.existsByPolicyIdAndClaimStatus(100, ClaimStatus.PENDING)).thenReturn(false);
        when(claimRepository.save(any(Claim.class))).thenReturn(claim);
 
        Claim savedClaim = claimService.submitClaim(claim, 1);
 
        assertNotNull(savedClaim);
        assertEquals(ClaimStatus.PENDING, savedClaim.getClaimStatus());
        assertEquals(policy, savedClaim.getPolicy());
    }
 
    @Test
    void testSubmitClaim_PolicyNotFound() {
        when(policyRepository.findById(100)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> claimService.submitClaim(claim, 1));
    }
 
    @Test
    void testSubmitClaim_DuplicatePending() {
        when(userRepository.findByUserId(1)).thenReturn(user);
        when(policyRepository.findById(100)).thenReturn(Optional.of(policy));
        //when(claimRepository.existsByPolicyIdAndClaimStatus(100, ClaimStatus.PENDING)).thenReturn(true);
 
        assertThrows(IllegalArgumentException.class, () -> claimService.submitClaim(claim, 1));
    }
}
 