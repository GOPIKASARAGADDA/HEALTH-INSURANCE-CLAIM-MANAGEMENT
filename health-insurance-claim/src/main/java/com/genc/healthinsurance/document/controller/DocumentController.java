package com.genc.healthinsurance.document.controller;
 
import com.genc.healthinsurance.claim.entity.Claim;
import com.genc.healthinsurance.document.entity.Document;
import com.genc.healthinsurance.document.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
 
@Controller
@RequestMapping("/documents")
public class DocumentController {
 
    @Autowired
    private DocumentService documentService;
 
    // ---------------- Upload Document Form ----------------
    @GetMapping("/upload-documents/{claimId}")
    public String showUploadForm(@PathVariable Integer claimId, Model model) {
    	Document document=new Document();
    	Claim claim=new Claim();
    	claim.setClaimId(claimId);
    	document.setClaim(claim);
    	model.addAttribute("document",document);

        return "documents/upload-documents";
    }
 
    // ---------------- Handle Upload ----------------
    @PostMapping("/upload")
    public String uploadDocument(@ModelAttribute Document document,
                                 @RequestParam("file") MultipartFile file,
                                 Model model) {
        documentService.uploadDocument(document, file);
        // Redirect to view claim page after upload
        return "redirect:/claims/" + document.getClaim().getClaimId();
    }
    
    
    
    // ----------------------------
    // View Document Details
    // ----------------------------
    @GetMapping("/view/{documentId}")
    public String getDocumentDetails(@PathVariable Long documentId, Model model) {
        Document doc = documentService.getDocumentById(documentId);
        model.addAttribute("document", doc);
        return "documents/view-document";
    }
 
    // ----------------------------
    // Delete Document
    // ----------------------------
    @PostMapping("/{documentId}/delete")
    public String deleteDocument(@PathVariable Long documentId) {
        Document doc = documentService.getDocumentById(documentId);
        Integer claimId = doc.getClaim().getClaimId();
        documentService.deleteDocument(documentId);
        return "redirect:/claims/" + claimId;
    }
}
 