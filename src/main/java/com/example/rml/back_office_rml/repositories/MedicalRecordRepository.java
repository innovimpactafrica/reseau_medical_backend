
package com.example.rml.back_office_rml.repositories;

import com.example.rml.back_office_rml.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

// ========================================
// REPOSITORY POUR DOSSIER MÉDICAL
// ========================================
@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    // Trouver le dossier médical d'un patient par son ID
    Optional<MedicalRecord> findByPatient_PatientId(Long patientId);

    // Vérifier si un patient a déjà un dossier médical
    boolean existsByPatient_PatientId(Long patientId);

   Optional <MedicalRecord> findByRecordNumber(String recordNumber);
}