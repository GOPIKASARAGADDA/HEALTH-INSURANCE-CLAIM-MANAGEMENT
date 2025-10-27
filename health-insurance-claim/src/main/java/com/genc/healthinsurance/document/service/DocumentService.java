package com.genc.healthinsurance.document.service;
 
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.genc.healthinsurance.claim.entity.Claim;
import com.genc.healthinsurance.claim.repository.ClaimRepository;
import com.genc.healthinsurance.document.entity.Document;
import com.genc.healthinsurance.document.entity.DocumentType;
import com.genc.healthinsurance.document.repository.DocumentRepository;
 
@Service
public class DocumentService {
	private static final Logger logger=LoggerFactory.getLogger(DocumentService.class);

	
 
    @Autowired
    private DocumentRepository documentRepository;
 
    @Autowired
    private ClaimRepository claimRepository;
 
    public Document uploadDocument(Document document, MultipartFile file) {
        if (document.getClaim() == null || document.getClaim().getClaimId() == null) {
        	logger.warn("claimId is null");

            throw new RuntimeException("Claim ID must not be null");
        }
     
        // Fetch the claim
        Claim claim = claimRepository.findById(document.getClaim().getClaimId())
                .orElseThrow(() -> new RuntimeException("Claim not found"));
        document.setClaim(claim);
     
        // Prepare upload directory (inside static folder)
        String uploadDir = "uploads/";
     
        // Clean filename
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
     fileName=fileName.replaceAll("\\s", "_");
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
     
            // Copy file to uploads folder
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
     
        } catch (IOException e) {
        	logger.error("file couldnt be uploaded");
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
     
        // Save the relative path (accessible from browser)
        String filePath = "/uploads/" + fileName;
     
        document.setDocumentPath(filePath);
        document.setDocumentName(file.getOriginalFilename());
        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename()).toUpperCase();
        DocumentType docType = DocumentType.valueOf(ext);
        document.setDocumentType(docType);
     
        logger.info("document upload success");
        return documentRepository.save(document);
    }
     
    public Document getDocumentById(Long documentId) {
       
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
    }
 
    public void deleteDocument(Long documentId) {
        Document doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
     
     // Extract relative path from documentPath
        String relativePath = doc.getDocumentPath(); // e.g., "/uploads/file.pdf"
         
        // Remove leading slash if exists
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }
         
        Path filePath = Paths.get(relativePath); // Now points correctly to project-root/uploads/file.pdf
        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                System.out.println("File deleted: " + filePath.toAbsolutePath());
            } else {
                System.out.println("File not found for deletion: " + filePath.toAbsolutePath());
            }
        } catch (IOException e) {
        	logger.error("file couldnt be deleted");
            throw new RuntimeException("Failed to delete file: " + e.getMessage());
        }
         
     logger.info("document deletion successful");
        // Delete from DB
        documentRepository.deleteById(documentId);
    }
     
}
 