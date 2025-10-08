package com.example.rml.back_office_rml.repositories;


import com.example.rml.back_office_rml.entities.Examination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// ========================================
// REPOSITORY POUR EXAMENS MÉDICAUX
// ========================================
@Repository
public interface ExaminationRepository extends JpaRepository<Examination, Long> {

    // Récupérer tous les examens d'un dossier
    List<Examination> findByMedicalRecord_Id(Long recordId);

    // Récupérer tous les examens prescrits par un médecin
    List<Examination> findByDoctor_DoctorId(Long doctorId);
}