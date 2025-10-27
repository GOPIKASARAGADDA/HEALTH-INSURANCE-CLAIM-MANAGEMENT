package com.genc.healthinsurance.policy.controller;
 
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.genc.healthinsurance.auth.entity.User;
import com.genc.healthinsurance.policy.entity.Policy;
import com.genc.healthinsurance.policy.entity.PolicyStatus;
import com.genc.healthinsurance.policy.service.PolicyService;

import jakarta.servlet.http.HttpSession;
 
@Controller
@RequestMapping("/policies")
public class PolicyController {
	private static final Logger logger=LoggerFactory.getLogger(PolicyController.class);

	
    @Autowired
    private PolicyService policyService;
 
    @Autowired
    private HttpSession session; // To get logged-in user
 
    // =======================
    // ADMIN PAGES
    // =======================
 
    // Create Policy page
    @GetMapping("/create")
    public String createPolicyPage(Model model) {

        Policy policy = new Policy();
logger.info("policy creation page");
     // Generate a random 6-digit number
         int randomNumber = (int)(Math.random() * 900000) + 100000; // ensures 6 digits
         policy.setPolicyNumber("POL-" + randomNumber);
        model.addAttribute("policy", policy);
        return "policy/create-policy";
    }
 
    // Handle Create Policy form submission
    @PostMapping("/create")
    public String createPolicySubmit(@ModelAttribute Policy policy) {
        logger.info(" request for policy creation");

        policyService.createPolicy(policy); // Admin creates policy without assigning user

        return "redirect:/policies/manage";
    }
 
    // Manage Policies page
    @GetMapping("/manage")
    public String managePolicyPage(Model model) {
        logger.info(" manage policies page");

        List<Policy> policies = policyService.getAllPolicies();
        model.addAttribute("policies", policies);
        return "policy/manage-policy";
    }
 
    // Handle Update Policy (via manage page)
    @PostMapping("/update/{policyId}")
    public String updatePolicy(@PathVariable Integer policyId,
                               @RequestParam Double coverageAmount,
                               @RequestParam PolicyStatus policyStatus) {
        logger.info(" request for updating policy with policyId {}",policyId);

        Policy updated = new Policy();
        updated.setCoverageAmount(coverageAmount);
        updated.setPolicyStatus(policyStatus);
        policyService.updatePolicy(policyId, updated);
        return "redirect:/policies/manage";
    }
 
    // Handle Delete Policy
    @DeleteMapping("/delete/{policyId}")
    public String deletePolicy(@PathVariable Integer policyId) {
        logger.info(" request for deleting policy with policyId {}",policyId);

        policyService.deletePolicy(policyId);
        return "redirect:/policies/manage";
    }
 
    // =======================
    // POLICY HOLDER PAGES
    // =======================
 
    //  View All Active Policies (for enrollment)
    @GetMapping("/view")
    public String viewPolicies(Model model) {
        logger.info(" request for viewing all active policies to enroll");

        List<Policy> activePolicies = policyService.getActivePolicies(); // Only ACTIVE policies
        model.addAttribute("policies", activePolicies);
        return "policy/view-policies";
    }
 
    // Handle enrollment of a policy by current user
    @PostMapping("/enroll/{policyId}")
    public String enrollPolicy(@PathVariable Integer policyId) {
        logger.info(" request for enrollin in policy with policyId {}",policyId);

        User user = (User) session.getAttribute("loggedInUser"); // Session stores user
        policyService.enrollPolicy(user.getUserId(), policyId);
        return "redirect:/policies/my-policies";
    }
 
    //  My Policies page (policies enrolled by user)
    @GetMapping("/my-policies")
    public String myPolicies(Model model) {

        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        logger.info(" request for showing policies enrolled by user with userId {}",userId);

        Set<Policy> myPolicies = policyService.getPoliciesByUser(userId);
        model.addAttribute("myPolicies", myPolicies);
        return "policy/my-policies";
    }
 
    //  View details of a specific policy (for Policy Holder)
    @GetMapping("/details/{policyId}")
    public String viewPolicyDetails(@PathVariable Integer policyId, Model model) {
        logger.info(" request for viewing policy details with policyId {}",policyId);

        Policy policy = policyService.getPolicyDetails(policyId)
                .orElseThrow(() -> new RuntimeException("Policy not found"));
        model.addAttribute("policy", policy);
        return "policy/policy-details"; // Create this Thymeleaf template
    }
}
 