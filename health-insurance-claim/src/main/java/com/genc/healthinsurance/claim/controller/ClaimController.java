package com.genc.healthinsurance.claim.controller;
 
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
import com.genc.healthinsurance.policy.service.PolicyService;

import jakarta.servlet.http.HttpSession;
 
@Controller
@RequestMapping("/claims")
public class ClaimController {
 
    @Autowired
    private ClaimService claimService;
 
    @Autowired
    private PolicyService policyService;
    
    @Autowired
    private UserService userService;
 
    // ---------------- Step 1: Submit Claim ----------------
    @GetMapping("/submit")
    public String showSubmitClaimForm(Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        if (userId==null) {
        	return "redirect:/home";
        }
        List<Policy>userPolicies=policyService.getPoliciesByUser(userId);
        model.addAttribute("policies",userPolicies ); // method in PolicyService
        model.addAttribute("claim", new Claim());
        return "claims/submit-claim";
    }
 
    @PostMapping("/submit")
    public String submitClaim(@ModelAttribute Claim claim, Model model) {
        Claim savedClaim = claimService.submitClaim(claim);
        return "redirect:/documents/upload-documents/" + savedClaim.getClaimId();
    }
    
    @GetMapping("/my-claims")
    public String viewMyClaims(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        if (userId == null) {
            return "redirect:/home";
        }
     
//        // Find the policyholder’s userId using username
//        User user = userService.getUserByUsername(username);
//        if (user == null) {
//            return "redirect:/login";
//        }
     
        // Get claims for that user only
        List<Claim> myClaims = claimService.getClaimsByUserId(userId);
        model.addAttribute("claims", myClaims);
        return "claims/my-claims";
    }
 
    // ---------------- View Claim ----------------
    @GetMapping("/{claimId}")
    public String getClaimDetails(@PathVariable Integer claimId, Model model) {
        Optional<Claim> claimOpt = claimService.getClaimDetails(claimId);
        
        if (claimOpt.isPresent()) {
        	
            model.addAttribute("claim", claimOpt.get());
            return "claims/view-claim";
        } else {
            model.addAttribute("error", "Claim not found");
            return "error";
        }
    }
 
    // ---------------- Update Claim Status ----------------
    @PostMapping("/{claimId}/status")
    public String updateClaimStatus(@PathVariable Integer claimId,
                                    @RequestParam ClaimStatus status) {
        Claim updatedClaim = claimService.updateClaimStatus(claimId, status);
        return "redirect:/claims/" + updatedClaim.getClaimId();
    }
 
    // ---------------- View All Claims by Policy ----------------
    @GetMapping("/policy/{policyId}")
    public String getAllClaimsByPolicy(@PathVariable Integer policyId, Model model) {
        List<Claim> claims = claimService.getAllClaimsByPolicy(policyId);
        model.addAttribute("claims", claims);
        return "claims/list-claims";
    }
    
     
}
 