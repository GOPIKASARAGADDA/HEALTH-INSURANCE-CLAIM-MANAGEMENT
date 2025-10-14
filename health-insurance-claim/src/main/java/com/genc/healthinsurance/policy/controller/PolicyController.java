package com.genc.healthinsurance.policy.controller;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.genc.healthinsurance.auth.entity.User;
import com.genc.healthinsurance.policy.entity.Policy;
import com.genc.healthinsurance.policy.service.PolicyService;
 
import java.util.List;
import java.util.Optional;
 
@RestController
@RequestMapping("/api/policies")
public class PolicyController {
 
    @Autowired
    private PolicyService policyService;
 
    @PostMapping("/create/{userId}")
    public Policy createPolicy(@PathVariable Integer userId, @RequestBody Policy policyData) {
        return policyService.createPolicy(userId, policyData);
    }
 
    @PutMapping("/update/{policyId}")
    public Policy updatePolicy(@PathVariable Integer policyId, @RequestBody Policy policyData) {
        return policyService.updatePolicy(policyId, policyData);
    }
 
    @GetMapping("/{policyId}")
    public Optional<Policy> getPolicyDetails(@PathVariable Integer policyId) {
        return policyService.getPolicyDetails(policyId);
    }
 
    @GetMapping
    public List<Policy> getAllPolicies() {
        return policyService.getAllPolicies();
    }
 
    @DeleteMapping("/delete/{policyId}")
    public void deletePolicy(@PathVariable Integer policyId) {
        policyService.deletePolicy(policyId);
    }
    @GetMapping("/{userId}")
    public List<Policy> getPoliciesByUser(@PathVariable Integer userId) {
     
        return policyService.getPoliciesByUser(userId);
    }
}