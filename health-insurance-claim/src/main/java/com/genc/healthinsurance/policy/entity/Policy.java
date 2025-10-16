package com.genc.healthinsurance.policy.entity;
 
import java.time.LocalDate;

import com.genc.healthinsurance.auth.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

 
@Entity
@Table(name = "policy")
public class Policy {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer policyId;
 
    @Column(nullable = false, unique = true)
    private String policyNumber;
 
    @ManyToOne
    @JoinColumn(name = "policyHolderId", nullable = false)
    private User policyHolder; // FK → User table
 
    @Column(nullable = false)
    private Double coverageAmount;
 
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PolicyStatus policyStatus; // ACTIVE / INACTIVE / CANCELLED
 
    @Column(nullable = false)
    private LocalDate createdDate = LocalDate.now();

	public Integer getPolicyId() {
		return policyId;
	}

	public void setPolicyId(Integer policyId) {
		this.policyId = policyId;
	}

	public String getPolicyNumber() {
		return policyNumber;
	}

	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}

	public User getPolicyHolder() {
		return policyHolder;
	}

	public void setPolicyHolder(User policyHolder) {
		this.policyHolder = policyHolder;
	}

	public Double getCoverageAmount() {
		return coverageAmount;
	}

	public void setCoverageAmount(Double coverageAmount) {
		this.coverageAmount = coverageAmount;
	}

	public PolicyStatus getPolicyStatus() {
		return policyStatus;
	}

	public void setPolicyStatus(PolicyStatus policyStatus) {
		this.policyStatus = policyStatus;
	}

	public LocalDate getCreateDate() {
		return createdDate;
	}

	public void setCreateDate(LocalDate createdDate) {
		this.createdDate = createdDate;
	}
    
    
}
 