package com.example.rml.back_office_rml.repositories;

import com.example.rml.back_office_rml.entities.FamilyHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// ========================================
// REPOSITORY POUR ANTÉCÉDENTS FAMILIAUX
// ========================================
@Repository
public interface FamilyHistoryRepository extends JpaRepository<FamilyHistory, Long> {

    // Récupérer tous les antécédents familiaux d'un dossier
    List<FamilyHistory> findByMedicalRecord_Id(Long recordId);
}