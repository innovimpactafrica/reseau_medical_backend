package com.example.rml.back_office_rml.repositories;

import com.example.rml.back_office_rml.entities.HealthCenterDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HealthCenterDocumentRepository extends JpaRepository<HealthCenterDocument, Long> {

    List<HealthCenterDocument> findByHealthCenter_CenterId(Long centerId);
}
