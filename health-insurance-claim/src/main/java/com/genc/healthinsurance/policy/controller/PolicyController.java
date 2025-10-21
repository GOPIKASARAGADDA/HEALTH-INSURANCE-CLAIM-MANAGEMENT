package com.genc.healthinsurance.policy.controller;
 
import com.genc.healthinsurance.auth.entity.User;
import com.genc.healthinsurance.policy.entity.Policy;
import com.genc.healthinsurance.policy.entity.PolicyStatus;
import com.genc.healthinsurance.policy.service.PolicyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
import java.util.Set;
 
@Controller
@RequestMapping("/policies")
public class PolicyController {
 
    @Autowired
    private PolicyService policyService;
 
    @Autowired
    private HttpSession session; // To get logged-in user
 
    // =======================
    // ADMIN PAGES
    // =======================
 
    // 1️⃣ Create Policy page
    @GetMapping("/create")
    public String createPolicyPage(Model model) {
        model.addAttribute("policy", new Policy());
        return "policy/create-policy";
    }
 
    // Handle Create Policy form submission
    @PostMapping("/create")
    public String createPolicySubmit(@ModelAttribute Policy policy) {

        policyService.createPolicy(policy); // Admin creates policy without assigning user
        return "redirect:/policies/manage";
    }
 
    // 2️⃣ Manage Policies page
    @GetMapping("/manage")
    public String managePolicyPage(Model model) {
        List<Policy> policies = policyService.getAllPolicies();
        model.addAttribute("policies", policies);
        return "policy/manage-policy";
    }
 
    // Handle Update Policy (via manage page)
    @PostMapping("/update/{policyId}")
    public String updatePolicy(@PathVariable Integer policyId,
                               @RequestParam Double coverageAmount,
                               @RequestParam PolicyStatus policyStatus) {
        Policy updated = new Policy();
        updated.setCoverageAmount(coverageAmount);
        updated.setPolicyStatus(policyStatus);
        policyService.updatePolicy(policyId, updated);
        return "redirect:/policies/manage";
    }
 
    // Handle Delete Policy
    @DeleteMapping("/delete/{policyId}")
    public String deletePolicy(@PathVariable Integer policyId) {
        policyService.deletePolicy(policyId);
        return "redirect:/policies/manage";
    }
 
    // =======================
    // POLICY HOLDER PAGES
    // =======================
 
    // 3️⃣ View All Active Policies (for enrollment)
    @GetMapping("/view")
    public String viewPolicies(Model model) {
        List<Policy> activePolicies = policyService.getActivePolicies(); // Only ACTIVE policies
        model.addAttribute("policies", activePolicies);
        return "policy/view-policies";
    }
 
    // Handle enrollment of a policy by current user
    @PostMapping("/enroll/{policyId}")
    public String enrollPolicy(@PathVariable Integer policyId) {
        User user = (User) session.getAttribute("loggedInUser"); // Session stores user
        policyService.enrollPolicy(user.getUserId(), policyId);
        return "redirect:/policies/my-policies";
    }
 
    // 4️ My Policies page (policies enrolled by user)
    @GetMapping("/my-policies")
    public String myPolicies(Model model) {
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        Set<Policy> myPolicies = policyService.getPoliciesByUser(userId);
        model.addAttribute("myPolicies", myPolicies);
        return "policy/my-policies";
    }
 
    // 5️ View details of a specific policy (for Policy Holder)
    @GetMapping("/details/{policyId}")
    public String viewPolicyDetails(@PathVariable Integer policyId, Model model) {
        Policy policy = policyService.getPolicyDetails(policyId)
                .orElseThrow(() -> new RuntimeException("Policy not found"));
        model.addAttribute("policy", policy);
        return "policy/policy-details"; // Create this Thymeleaf template
    }
}
 