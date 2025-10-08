package com.example.rml.back_office_rml.repositories;

import com.example.rml.back_office_rml.entities.MedicalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// ========================================
// REPOSITORY POUR ANTÉCÉDENTS MÉDICAUX
// ========================================
@Repository
public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory, Long> {

    // Récupérer tous les antécédents médicaux d'un dossier
    List<MedicalHistory> findByMedicalRecord_Id(Long recordId);
}