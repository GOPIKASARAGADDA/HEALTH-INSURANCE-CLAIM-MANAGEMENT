package com.genc.healthinsurance.document.entity;
 
import com.genc.healthinsurance.claim.entity.Claim;

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
 
    @Enumerated(EnumType.STRING)
    private DocumentType documentType;
 
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

	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	public String getDocumentPath() {
		return documentPath;
	}

	public void setDocumentPath(String documentPath) {
		this.documentPath = documentPath;
	}
    
    
}