package com.genc.healthinsurance.document.service;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.genc.healthinsurance.claim.entity.Claim;
import com.genc.healthinsurance.claim.repository.ClaimRepository;
import com.genc.healthinsurance.document.entity.Document;
import com.genc.healthinsurance.document.repository.DocumentRepository;
 
@Service
public class DocumentService {
 
    @Autowired
    private DocumentRepository documentRepository;
 
    @Autowired
    private ClaimRepository claimRepository;
 
    public Document uploadDocument(Document document, MultipartFile file) {
        if (document.getClaim() == null || document.getClaim().getClaimId() == null) {
            throw new RuntimeException("Claim ID must not be null");
        }
     
        // Fetch the claim
        Claim claim = claimRepository.findById(document.getClaim().getClaimId())
                .orElseThrow(() -> new RuntimeException("Claim not found"));
     
        document.setClaim(claim);
     
        // Handle file upload (example: store in /uploads folder)
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String filePath = "/uploads/" + fileName;
        // TODO: Save file physically or to cloud here
     
        document.setDocumentPath(filePath);
        document.setDocumentName(document.getDocumentName());
        document.setDocumentType(document.getDocumentType());
     
        return documentRepository.save(document);
    }
     
 
    public Document getDocumentById(Long documentId) {
       
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
    }
 
    public void deleteDocument(Long documentId) {
        documentRepository.deleteById(documentId);
    }
}
 