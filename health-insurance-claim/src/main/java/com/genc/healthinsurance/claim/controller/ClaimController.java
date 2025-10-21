package com.genc.healthinsurance.claim.controller;
 
import java.util.List;
import java.util.Optional;

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
import com.genc.healthinsurance.auth.service.AuthService;
import com.genc.healthinsurance.claim.entity.Claim;
import com.genc.healthinsurance.claim.entity.ClaimStatus;
import com.genc.healthinsurance.claim.service.ClaimService;
import com.genc.healthinsurance.policy.service.PolicyService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
 
@Controller
@RequestMapping("/claims")
public class ClaimController {
 
    @Autowired
    private ClaimService claimService;
 
    @Autowired
    private AuthService userService;
 
    @Autowired
    private PolicyService policyService;
 
    // ---------------- Show submit claim form ----------------
    @GetMapping("/submit")
    public String showSubmitClaimForm(Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        String userRole = (String) session.getAttribute("userRole");
        if (userId == null) return "redirect:/home";
 
        model.addAttribute("userRole", userRole);
        model.addAttribute("claim", new Claim());
 
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if ("AGENT".equalsIgnoreCase(userRole)) {
            model.addAttribute("policies", policyService.getAllPolicies());
        } else if (loggedInUser != null) {
            model.addAttribute("policies", policyService.getPoliciesByUser(userId));
        }
 
        return "claims/submit-claim";
    }
 
    // ---------------- Submit claim ----------------
    @PostMapping("/submit")
    public String submitClaim(@Valid @ModelAttribute Claim claim, BindingResult result, HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        String userRole = (String) session.getAttribute("userRole");
        if (userId == null) return "redirect:/home";
     
        try {
            Claim savedClaim;
            if ("AGENT".equalsIgnoreCase(userRole)) {
                savedClaim = claimService.submitClaimByAgent(claim);
                return "redirect:/claims/" + savedClaim.getClaimId();
            } else {
                savedClaim = claimService.processClaimSubmission(claim, userId, userRole);
                return "redirect:/claims/my-claims";
            }
        } catch (RuntimeException ex) {
            result.reject("error.claim", ex.getMessage());
     
            // Re-populate policies for the form based on role
            model.addAttribute("userRole", userRole);
            if ("AGENT".equalsIgnoreCase(userRole)) {
                model.addAttribute("policies", policyService.getAllPolicies());
            } else {
                model.addAttribute("policies", policyService.getPoliciesByUser(userId));
            }
     
            return "claims/submit-claim";
        }
    }
 
    // ---------------- View my claims ----------------
    @GetMapping("/my-claims")
    public String viewMyClaims(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        if (userId == null) return "redirect:/home";
 
        String userRole = (String) session.getAttribute("userRole");
        model.addAttribute("claims", claimService.getClaimsByUserId(userId));
        model.addAttribute("userRole", userRole);
        return "claims/my-claims";
    }
 
    // ---------------- View claim details ----------------
    @GetMapping("/{claimId}")
    public String getClaimDetails(@PathVariable Integer claimId, Model model, HttpSession session) {
        Optional<Claim> claimOpt = claimService.getClaimDetails(claimId);
        if (claimOpt.isPresent()) {
            model.addAttribute("claim", claimOpt.get());
            model.addAttribute("userRole", session.getAttribute("userRole"));
            return "claims/view-claim";
        } else {
            model.addAttribute("error", "Claim not found");
            return "error";
        }
    }
 
    // ---------------- Update claim status ----------------
    @PostMapping("/{claimId}/status")
    public String updateClaimStatus(@PathVariable Integer claimId, ClaimStatus status, HttpSession session) {
        claimService.updateClaimStatus(claimId, status);
        String role = (String) session.getAttribute("userRole");
        if ("CLAIM_ADJUSTER".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role)) {
            return "redirect:/claims/review";
        }
        return "redirect:/claims/my-claims";
    }
 
    // ---------------- Review claims (Admin & Adjuster) ----------------
    @GetMapping("/review")
    public String reviewClaims(HttpSession session, Model model) {
        String userRole = (String) session.getAttribute("userRole");
        Integer loggedInUserId = (Integer) session.getAttribute("loggedInUserId");
 
        List<Claim> claims = switch (userRole.toUpperCase()) {
            case "ADMIN" -> claimService.getAllClaims();
            case "CLAIM_ADJUSTER" -> claimService.getClaimsByAdjuster(loggedInUserId);
            default -> null;
        };
 
        if (claims == null) return "redirect:/claims/my-claims";
 
        model.addAttribute("claims", claims);
        model.addAttribute("userRole", userRole);
 
        if ("ADMIN".equalsIgnoreCase(userRole)) {
            model.addAttribute("adjusters", userService.getAllAdjusters());
        }
 
        return "claims/review-claims";
    }
 
    // ---------------- Assign adjuster (Admin only) ----------------
    @PostMapping("/assign")
    public String assignAdjuster(@RequestParam Integer claimId, @RequestParam Integer adjusterId) {
        claimService.assignAdjuster(claimId, adjusterId);
        return "redirect:/claims/review";
    }
 
    // ---------------- View all claims for a policy (Policyholder) ----------------
    @GetMapping("/policy")
    public String viewClaimsByPolicyholder(@RequestParam(value = "policyId", required = false) Integer policyId,
                                           HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        model.addAttribute("userRole", "POLICYHOLDER");
 
        List<Claim> claims = claimService.getClaimsForUserByPolicy(userId, policyId);
 
        model.addAttribute("claims", claims);
        model.addAttribute("selectedPolicyId", policyId);
        return "claims/my-claims";
    }
}
 