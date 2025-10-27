package com.genc.healthinsurancetest.doc;
 
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
 
import java.util.Optional;
 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import com.genc.healthinsurance.claim.entity.Claim;
import com.genc.healthinsurance.claim.repository.ClaimRepository;
import com.genc.healthinsurance.document.entity.Document;
import com.genc.healthinsurance.document.entity.DocumentType;
import com.genc.healthinsurance.document.repository.DocumentRepository;
import com.genc.healthinsurance.document.service.DocumentService;
 
public class DocumentServiceTest {
 
    @Mock private DocumentRepository documentRepository;
    @Mock private ClaimRepository claimRepository;
 
    @InjectMocks private DocumentService documentService;
 
    private Claim claim;
    private Document document;
    private MockMultipartFile file;
 
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
 
        claim = new Claim();
        claim.setClaimId(1);
 
        document = new Document();
        document.setClaim(claim);
 
        file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "Dummy Content".getBytes()
        );
    }
 
    @Test
    void testUploadDocument() {
        when(claimRepository.findById(1)).thenReturn(Optional.of(claim));
        when(documentRepository.save(any(Document.class))).thenAnswer(i -> i.getArgument(0));
 
        Document saved = documentService.uploadDocument(document, file);
 
        assertNotNull(saved);
        assertEquals("/uploads/test.pdf", saved.getDocumentPath());
        assertEquals("test.pdf", saved.getDocumentName());
        assertEquals(DocumentType.PDF, saved.getDocumentType());
    }
 
    @Test
    void testGetDocumentById() {
        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));
 
        Document d = documentService.getDocumentById(1L);
        assertEquals(document, d);
    }
 
    @Test
    void testDeleteDocument() {
        document.setDocumentPath("/uploads/test.pdf");
        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));
        doNothing().when(documentRepository).deleteById(1L);
 
        assertDoesNotThrow(() -> documentService.deleteDocument(1L));
        verify(documentRepository, times(1)).deleteById(1L);
    }
}
  