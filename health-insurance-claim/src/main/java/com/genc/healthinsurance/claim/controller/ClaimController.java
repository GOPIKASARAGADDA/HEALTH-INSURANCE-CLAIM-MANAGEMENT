package com.genc.healthinsurance.claim.controller;
 
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.genc.healthinsurance.auth.entity.User;
import com.genc.healthinsurance.auth.service.UserService;
import com.genc.healthinsurance.claim.entity.Claim;
import com.genc.healthinsurance.claim.entity.ClaimStatus;
import com.genc.healthinsurance.claim.service.ClaimService;
import com.genc.healthinsurance.policy.entity.Policy;
import com.genc.healthinsurance.policy.entity.PolicyStatus;
import com.genc.healthinsurance.policy.service.PolicyService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
 
@Controller
@RequestMapping("/claims")
public class ClaimController {
 
    @Autowired
    private ClaimService claimService;
 
    @Autowired
    private UserService userService;
    
    @Autowired
    private PolicyService policyService;
 
    // ---------------- Step 1: Show submit claim form ----------------
    @GetMapping("/submit")
    public String showSubmitClaimForm(Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        String userRole = (String) session.getAttribute("userRole");
        if (userId == null) {
            return "redirect:/home";
        }
 
        model.addAttribute("userRole", userRole);
        model.addAttribute("claim", new Claim());
 
     // For both agent and policyholder, show available policies
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if ("AGENT".equalsIgnoreCase(userRole)) {
            // Agent: get all policies
            model.addAttribute("policies", claimService.getAllPolicies());
        } else {
            // Policyholder: get only their policies
        
            if (loggedInUser != null) {
                model.addAttribute("policies", claimService.getPoliciesByUser(loggedInUser));
            }
        }
     
     
        return "claims/submit-claim";
    }
 
    // ---------------- Step 2: Submit claim ----------------
    @PostMapping("/submit")
    public String submitClaim(@Valid @ModelAttribute Claim claim,
                              BindingResult result,
                              HttpSession session,
                              Model model) {
     
        String userRole = (String) session.getAttribute("userRole");
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
     
        // Fetch the policy using Optional
        Optional<Policy> optionalPolicy = policyService.getPolicyDetails(claim.getPolicy().getPolicyId());
     
        if (optionalPolicy.isEmpty()) {
            result.rejectValue("policy", "error.claim", "Selected policy does not exist");
        } else {
            Policy policy = optionalPolicy.get();
     
            // Check if policy is active
            if (policy.getPolicyStatus() != PolicyStatus.ACTIVE) {
                result.rejectValue("policy", "error.claim", "Cannot submit claim: Policy is not active");
            }
     
            // Check claim amount vs coverage
            if (claim.getClaimAmount() != null && claim.getClaimAmount() > policy.getCoverageAmount()) {
                result.rejectValue("claimAmount", "error.claim", "Claim amount exceeds policy coverage");
            }
     
            // Set the policy object in claim
            claim.setPolicy(policy);
        }
     
        // If there are validation errors, return to the form with errors
        if (result.hasErrors()) {
            List<Policy> policies = policyService.getPoliciesByUser(userId);
            model.addAttribute("policies", policies);
            return "claims/submit-claim";
        }
     
        // Set claim defaults
        claim.setClaimDate(LocalDate.now());
        claim.setClaimStatus(ClaimStatus.PENDING);
     
        if ("AGENT".equalsIgnoreCase(userRole)) {
            claimService.submitClaimByAgent(claim);
            return "redirect:/claims/" + claim.getClaimId();
        } else {
            claimService.submitClaim(claim);
            return "redirect:/claims/my-claims";
        }
    }
     
     
 
    // ---------------- Step 3: View my claims ----------------
    @GetMapping("/my-claims")
    public String viewMyClaims(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        if (userId == null) {
            return "redirect:/home";
        }
 
        String userRole = (String) session.getAttribute("userRole");
        List<Claim> myClaims;
            // Claims submitted by this policyholder
            myClaims = claimService.getClaimsByUserId(userId);
        
 
        model.addAttribute("claims", myClaims);
        model.addAttribute("userRole", userRole);

        return "claims/my-claims";
    }
 
    // ---------------- Step 4: View claim details ----------------
    @GetMapping("/{claimId}")
    public String getClaimDetails(@PathVariable Integer claimId, Model model,HttpSession session) {
        Optional<Claim> claimOpt = claimService.getClaimDetails(claimId);
 
        if (claimOpt.isPresent()) {
        	Claim claim=claimOpt.get();
            model.addAttribute("claim", claim);
            String userRole = (String) session.getAttribute("userRole");
            model.addAttribute("userRole", userRole);

            return "claims/view-claim";
        } else {
            model.addAttribute("error", "Claim not found");
            return "error";
        }
    }
 
    // ---------------- Step 5: Update claim status ----------------
    @PostMapping("/{claimId}/status")
    public String updateClaimStatus(@PathVariable Integer claimId, ClaimStatus status, HttpSession session) {
        claimService.updateClaimStatus(claimId, status);
        String role = (String) session.getAttribute("userRole");
        if ("CLAIM_ADJUSTER".equalsIgnoreCase(role)) {
            return "redirect:/claims/review"; // Adjuster review page
        } else if ("ADMIN".equalsIgnoreCase(role)) {
            return "redirect:/claims/review"; // Admin review page
        } else {
            return "redirect:/claims/my-claims"; // Policyholder
        }
    }
 
 // ---------------- Review claims (Admin & Adjuster) ----------------
    @GetMapping("/review")
    public String reviewClaims(HttpSession session, Model model) {
        String userRole = (String) session.getAttribute("userRole");
        Integer loggedInUserId = (Integer) session.getAttribute("loggedInUserId");
     
        List<Claim> claims;
     
        if ("ADMIN".equalsIgnoreCase(userRole)) {
            // Admin sees all claims
            claims = claimService.getAllClaims();
        } else if ("CLAIM_ADJUSTER".equalsIgnoreCase(userRole)) {
            // Adjuster sees only assigned claims
            claims = claimService.getClaimsByAdjuster(loggedInUserId);
        } else {
            // For others, redirect to their normal pages
            return "redirect:/claims/my-claims";
        }
     
        // Add common attributes
        model.addAttribute("claims", claims);
        model.addAttribute("userRole", userRole);
     
        // Fetch available adjusters for admin to assign
        if ("ADMIN".equalsIgnoreCase(userRole)) {
            model.addAttribute("adjusters", userService.getAllAdjusters());
        }
     
        return "claims/review-claims";
    }
    @PostMapping("/assign")
    public String assignAdjuster(Integer claimId, Integer adjusterId) {
        claimService.assignAdjuster(claimId, adjusterId);
        return "redirect:/claims/review";
    }
	
	  // ---------------- Step 6: View all claims by policy ----------------
	  
    @GetMapping("/policy")
    public String viewClaimsByPolicyholder(@RequestParam(value = "policyId", required = false) Integer policyId,
                                           HttpSession session,
                                           Model model) {
        // Get logged-in user info
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        model.addAttribute("userRole", "POLICYHOLDER");
     
        // Fetch all claims for this user
        List<Claim> claims = claimService.getClaimsByUserId(userId);
     
        // Filter by policy if policyId is provided
        if (policyId != null) {
            claims = claims.stream()
                           .filter(c -> c.getPolicy()!=null && policyId.equals(c.getPolicy().getPolicyId())) // primitive comparison
                           .collect(Collectors.toList());
        }
     
        model.addAttribute("claims", claims);
     
        // Keep selected value for UI
        model.addAttribute("selectedPolicyId", policyId);
     
        return "claims/my-claims";
    }
     

     
	
}
 