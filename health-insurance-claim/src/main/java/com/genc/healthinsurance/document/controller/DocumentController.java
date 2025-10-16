package com.genc.healthinsurance.document.controller;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.genc.healthinsurance.claim.entity.Claim;
import com.genc.healthinsurance.document.entity.Document;
import com.genc.healthinsurance.document.service.DocumentService;

import jakarta.servlet.http.HttpSession;
 
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
    public String getDocumentDetails(@PathVariable Long documentId, Model model, HttpSession session) {
        Document doc = documentService.getDocumentById(documentId);
        model.addAttribute("document", doc);
        
        // Pass role from session (if you store logged-in user)
        String role = (String) session.getAttribute("userRole"); // or get from user object
        model.addAttribute("userRole", role);
     
        return "documents/view-document";
    }
    // ----------------------------
    // Delete Document
    // ----------------------------
    @PostMapping("/delete/{documentId}")
    public String deleteDocument(@PathVariable Long documentId,HttpSession session) {
    	String role=(String) session.getAttribute("userRole");
    	if(!"ADMIN".equalsIgnoreCase(role) && !"POLICYHOLDER".equalsIgnoreCase(role)) {
    		return "redirect:/home";
    	}
        Document doc = documentService.getDocumentById(documentId);
        Integer claimId = doc.getClaim().getClaimId();
        documentService.deleteDocument(documentId);
        
        if("ADMIN".equalsIgnoreCase(role)) {
        	return "redirect:/claims/review";
        }else {
        return "redirect:/claims/" + claimId;
    }
    }
}
 