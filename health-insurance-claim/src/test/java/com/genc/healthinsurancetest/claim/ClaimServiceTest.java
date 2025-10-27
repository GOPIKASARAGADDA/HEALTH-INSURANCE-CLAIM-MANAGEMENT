package com.genc.healthinsurancetest.claim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
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
 
class ClaimServiceTest {
 
    @Mock
    private ClaimRepository claimRepository;
 
    @Mock
    private PolicyRepository policyRepository;
 
    @Mock
    private UserRepository userRepository;
 
    @InjectMocks
    private ClaimService claimService;
 
    private Policy activePolicy;
    private User user;
    private Claim claim;
 
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
 
        user = new User();
        user.setUserId(1);
        user.setUsername("john");
 
        activePolicy = new Policy();
        activePolicy.setPolicyId(101);
        activePolicy.setCoverageAmount(10000.0);
        activePolicy.setPolicyStatus(PolicyStatus.ACTIVE);
 
        claim = new Claim();
        claim.setClaimId(1);
        claim.setPolicy(activePolicy);
        claim.setUser(user);
        claim.setClaimAmount(5000.0);
    }
 
    @Test
    void testProcessClaimSubmission_Success() {
        when(policyRepository.findById(101)).thenReturn(Optional.of(activePolicy));
        when(userRepository.findByUserId(1)).thenReturn(user);
        when(policyRepository.existsByUserAndPolicy(user, activePolicy)).thenReturn(true);
        when(claimRepository.save(any(Claim.class))).thenAnswer(invocation -> invocation.getArgument(0));
 
        Claim result = claimService.processClaimSubmission(claim, 1, "POLICYHOLDER");
 
        assertNotNull(result); //claim submission success
        assertEquals(ClaimStatus.PENDING, result.getClaimStatus());//testing on submission status
        assertEquals(user, result.getUser()); //test user
        assertEquals(activePolicy, result.getPolicy()); //test policy under which claim
        verify(claimRepository, times(1)).save(claim); //saved only once
    }
 
    @Test
    void testUpdateClaimStatus_Success() {
        when(claimRepository.findById(1)).thenReturn(Optional.of(claim));
        when(claimRepository.save(any(Claim.class))).thenAnswer(invocation -> invocation.getArgument(0));
 
        Claim updated = claimService.updateClaimStatus(1, ClaimStatus.APPROVED);
 
        assertEquals(ClaimStatus.APPROVED, updated.getClaimStatus());//pending->approved
    }
 
    @Test
    void testGetClaimDetails_Found() {
        when(claimRepository.findById(1)).thenReturn(Optional.of(claim));
        Optional<Claim> result = claimService.getClaimDetails(1);
        assertTrue(result.isPresent()); //such a claim with id 1 exists ret true
    }
 
 
    @Test
    void testGetClaimsByUserId() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(claimRepository.findByUser(user)).thenReturn(List.of(claim));
 
        List<Claim> claims = claimService.getClaimsByUserId(1);//claims of userid 1
 
        assertEquals(1, claims.size()); //only one claim mocked
        verify(claimRepository, times(1)).findByUser(user);
    }
 

    @Test
    void testGetAllClaims() {
        when(claimRepository.findAll()).thenReturn(Arrays.asList(claim));
        List<Claim> claims = claimService.getAllClaims();
        assertEquals(1, claims.size()); //total 1 claims mocked
    }
}
 