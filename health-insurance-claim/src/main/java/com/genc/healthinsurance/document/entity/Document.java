package com.genc.healthinsurance.document.entity;
 
import jakarta.persistence.*;

import java.math.BigInteger;

import com.genc.healthinsurance.claim.entity.Claim;
 
@Entity

@Table(name = "document")
public class Document {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long documentId;
 
    @ManyToOne
    @JoinColumn(name = "claimId", nullable = false)
    private Claim claim; // FK → Claim
 
    @Column(nullable = false)
    private String documentName;
 
    @Column(nullable = false)
    private String documentType;
 
    @Column(nullable = false)
    private String documentPath; // stored path (file system or cloud)

	public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

	public Claim getClaim() {
		return claim;
	}

	public void setClaim(Claim claim) {
		this.claim = claim;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getDocumentPath() {
		return documentPath;
	}

	public void setDocumentPath(String documentPath) {
		this.documentPath = documentPath;
	}
    
    
}