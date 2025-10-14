package com.genc.healthinsurance.document.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.genc.healthinsurance.document.entity.Document;

import java.math.BigInteger;
import java.util.List;
import com.genc.healthinsurance.claim.entity.Claim;


public interface DocumentRepository extends JpaRepository<Document, Long> {
List<Document> findByClaim(Claim claim);
}
