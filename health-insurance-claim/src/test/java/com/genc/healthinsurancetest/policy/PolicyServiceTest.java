package com.genc.healthinsurancetest.policy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.genc.healthinsurance.auth.entity.User;
import com.genc.healthinsurance.auth.repository.UserRepository;
import com.genc.healthinsurance.policy.entity.Policy;
import com.genc.healthinsurance.policy.entity.PolicyStatus;
import com.genc.healthinsurance.policy.repository.PolicyRepository;
import com.genc.healthinsurance.policy.service.PolicyService;
 
public class PolicyServiceTest {
 
    @Mock private PolicyRepository policyRepository;
    @Mock private UserRepository userRepository;
 
    @InjectMocks private PolicyService policyService;
 
    private Policy policy;
    private User user;
 
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
 
        // Test policy
        policy = new Policy();
        policy.setPolicyId(1);
        policy.setCoverageAmount(10000.0);
        policy.setPolicyStatus(PolicyStatus.ACTIVE);
        policy.setCreateDate(LocalDate.now());
        policy.setEnrolledUsers(new HashSet<>());
 
        // Test user
        user = new User();
        user.setUserId(10);
        user.setEnrolledPolicies(new HashSet<>());
    }
 
    @Test
    void testCreatePolicy() {
        when(policyRepository.save(any(Policy.class))).thenReturn(policy);
 
        Policy created = policyService.createPolicy(policy);
 
        assertNotNull(created);
        assertEquals(10000.0, created.getCoverageAmount());
        verify(policyRepository, times(1)).save(policy);
    }
 
    @Test
    void testUpdatePolicy_Success() {
        when(policyRepository.findById(1)).thenReturn(Optional.of(policy));
        when(policyRepository.save(any(Policy.class))).thenReturn(policy);
 
        Policy updatedData = new Policy();
        updatedData.setCoverageAmount(20000.0);
        updatedData.setPolicyStatus(PolicyStatus.INACTIVE);
 
        Policy updated = policyService.updatePolicy(1, updatedData);
 
        assertEquals(20000.0, updated.getCoverageAmount());
        assertEquals(PolicyStatus.INACTIVE, updated.getPolicyStatus());
        verify(policyRepository, times(1)).save(policy);
    }
 
    @Test
    void testGetPolicyDetails_Found() {
        when(policyRepository.findById(1)).thenReturn(Optional.of(policy));
 
        Optional<Policy> opt = policyService.getPolicyDetails(1);
        assertTrue(opt.isPresent());
        assertEquals(1, opt.get().getPolicyId());
    }
 
    @Test
    void testDeletePolicy_Success() {
        when(policyRepository.existsById(1)).thenReturn(true);
        doNothing().when(policyRepository).deleteById(1);
 
        assertDoesNotThrow(() -> policyService.deletePolicy(1));
        verify(policyRepository, times(1)).deleteById(1);
    }
 
    @Test
    void testEnrollPolicy_Success() {
        when(userRepository.findById(10)).thenReturn(Optional.of(user));
        when(policyRepository.findById(1)).thenReturn(Optional.of(policy));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(policyRepository.save(any(Policy.class))).thenReturn(policy);
 
        assertDoesNotThrow(() -> policyService.enrollPolicy(10, 1));
        assertTrue(user.getEnrolledPolicies().contains(policy));
        assertTrue(policy.getEnrolledUsers().contains(user));
    }
 
    @Test
    void testGetPoliciesByUser() {
        user.getEnrolledPolicies().add(policy);
        when(userRepository.findById(10)).thenReturn(Optional.of(user));
 
        Set<Policy> policies = policyService.getPoliciesByUser(10);
        assertEquals(1, policies.size());
        assertTrue(policies.contains(policy));
    }
}
 