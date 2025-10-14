package com.genc.healthinsurance.policy.service;
 
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
import com.genc.healthinsurance.auth.entity.User;
import com.genc.healthinsurance.auth.repository.UserRepository;
import com.genc.healthinsurance.policy.entity.Policy;
import com.genc.healthinsurance.policy.repository.PolicyRepository;
 
@Service
public class PolicyService {
 
    @Autowired
    private PolicyRepository policyRepository;
 
    @Autowired
    private UserRepository userRepository;
 
    public Policy createPolicy(Integer userId, Policy policyData) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        policyData.setPolicyHolder(user);
        return policyRepository.save(policyData);
    }
 
    public Policy updatePolicy(Integer policyId, Policy updatedData) {
        Policy existing = policyRepository.findById(policyId)
                .orElseThrow(() -> new RuntimeException("Policy not found"));
        existing.setCoverageAmount(updatedData.getCoverageAmount());
        existing.setPolicyStatus(updatedData.getPolicyStatus());
        return policyRepository.save(existing);
    }
 
    public Optional<Policy> getPolicyDetails(Integer policyId) {
        return policyRepository.findById(policyId);
    }
 
    public List<Policy> getAllPolicies() {
        return policyRepository.findAll();
    }
 
    public void deletePolicy(Integer policyId) {
        policyRepository.deleteById(policyId);
    }
 
    // -----------------------------
    // New Method: Get policies by user ID
    // -----------------------------
    public List<Policy> getPoliciesByUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return policyRepository.findByPolicyHolder(user);
    }
}
 