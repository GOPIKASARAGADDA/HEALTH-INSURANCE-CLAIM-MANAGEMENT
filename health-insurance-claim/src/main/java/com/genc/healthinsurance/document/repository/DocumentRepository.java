package com.genc.healthinsurance.document.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.genc.healthinsurance.claim.entity.Claim;
import com.genc.healthinsurance.document.entity.Document;


public interface DocumentRepository extends JpaRepository<Document, Long> {
List<Document> findByClaim(Claim claim);
}
