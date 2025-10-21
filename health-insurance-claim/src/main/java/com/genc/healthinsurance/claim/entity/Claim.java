package com.genc.healthinsurance.claim.entity;
 
import java.time.LocalDate;
import java.util.List;
 
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import com.genc.healthinsurance.auth.entity.User;
import com.genc.healthinsurance.policy.entity.Policy;
import com.genc.healthinsurance.document.entity.Document;
 
@Entity
@Table(name = "claim")
public class Claim {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer claimId;
 
    @NotNull(message="Policy must be selected")
    
    @ManyToOne
    @JoinColumn(name = "policyId", nullable = false)
    private Policy policy;
    
    @ManyToOne
    @JoinColumn(name="userId")
    private User user;
 
    @NotNull(message="claim amount is required")
    @Min(value = 1,message="claim amount must be positive")
    @Column(nullable = false)
    private Double claimAmount;
 
    @Column(nullable = false)
    private LocalDate claimDate;
 
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimStatus claimStatus;
 
    @ManyToOne
    @JoinColumn(name = "adjusterId")
    private User adjuster; // Nullable until assigned
    
 
    // --------- NEW: One-to-Many mapping to Documents ---------
    @OneToMany(mappedBy = "claim", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Document> documents;
 
    // --------- Getters & Setters ---------
    
    
    
    public Integer getClaimId() {
        return claimId;
    }
 
    public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setClaimId(Integer claimId) {
        this.claimId = claimId;
    }
 
    public Policy getPolicy() {
        return policy;
    }
 
    public void setPolicy(Policy policy) {
        this.policy = policy;
    }
 
    public Double getClaimAmount() {
        return claimAmount;
    }
 
    public void setClaimAmount(Double claimAmount) {
        this.claimAmount = claimAmount;
    }
 
    public LocalDate getClaimDate() {
        return claimDate;
    }
 
    public void setClaimDate(LocalDate claimDate) {
        this.claimDate = claimDate;
    }
 
    public ClaimStatus getClaimStatus() {
        return claimStatus;
    }
 
    public void setClaimStatus(ClaimStatus claimStatus) {
        this.claimStatus = claimStatus;
    }
 
    public User getAdjuster() {
        return adjuster;
    }
 
    public void setAdjuster(User adjuster) {
        this.adjuster = adjuster;
    }
 
    public List<Document> getDocuments() {
        return documents;
    }
 
    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
}
 