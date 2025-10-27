package com.genc.healthinsurance.policy.service;
 
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.genc.healthinsurance.auth.entity.User;
import com.genc.healthinsurance.auth.repository.UserRepository;
import com.genc.healthinsurance.policy.entity.Policy;
import com.genc.healthinsurance.policy.entity.PolicyStatus;
import com.genc.healthinsurance.policy.repository.PolicyRepository;
 
@Service
public class PolicyService {
	private static final Logger logger=LoggerFactory.getLogger(PolicyService.class);

	
    @Autowired
    private PolicyRepository policyRepository;
 
    @Autowired
    private UserRepository userRepository;
 
    // ----------------- Admin -----------------
 
    public Policy createPolicy(Policy policyData) {
        policyData.setCreateDate(LocalDate.now());
    	if(policyData.getPolicyStatus()==null) { //if admin do not assign any status by def
            policyData.setPolicyStatus(PolicyStatus.ACTIVE);
        	}       
    	logger.info("policy created");
    	return policyRepository.save(policyData);
    }
 
    public Policy updatePolicy(Integer policyId, Policy updatedData) {
        Policy existing = policyRepository.findById(policyId)
                .orElseThrow(() -> new RuntimeException("Policy not found"));
        existing.setCoverageAmount(updatedData.getCoverageAmount());
        existing.setPolicyStatus(updatedData.getPolicyStatus());
    	logger.info("policy with id:{} updated",policyId);

        return policyRepository.save(existing);
    }
 
    public Optional<Policy> getPolicyDetails(Integer policyId) {
    	
        return policyRepository.findById(policyId);
    }
 
    public List<Policy> getAllPolicies() {
    	
        return policyRepository.findAll();
    }
 
    public void deletePolicy(Integer policyId) {
        if (!policyRepository.existsById(policyId)) {
        	logger.warn("no policy with Id:{}",policyId);
            throw new RuntimeException("Policy not found");
        }
    	logger.info("policy with id:{} deleted",policyId);

        policyRepository.deleteById(policyId);
    }
 
    // ----------------- Policy Holder -----------------
 
    // Enroll user in a policy
    public void enrollPolicy(Integer userId, Integer policyId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new RuntimeException("Policy not found"));
 
        user.getEnrolledPolicies().add(policy);
        policy.getEnrolledUsers().add(user);
        userRepository.save(user); // Save the enrollment
        logger.info("user:{} enrolled in policy:{}",userId,policyId);
        policyRepository.save(policy);
    }
 
    // Get all policies user is enrolled in
    public Set<Policy> getPoliciesByUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getEnrolledPolicies();
    }
 
    // Get all active policies for user to view/enroll
    public List<Policy> getActivePolicies() {
        return policyRepository.findAll()
                .stream()
                .filter(p -> p.getPolicyStatus() == PolicyStatus.ACTIVE)
                .toList();
    }
}
 