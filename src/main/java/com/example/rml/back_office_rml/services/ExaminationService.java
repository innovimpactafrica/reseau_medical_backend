package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.ExaminationDTO;

import java.util.List;

// ========================================
// SERVICE POUR EXAMENS MÉDICAUX
// ========================================
public interface ExaminationService {

    // Ajouter un examen médical
    ExaminationDTO addExamination(ExaminationDTO dto);

    // Mettre à jour un examen médical
    ExaminationDTO updateExamination(Long examinationId, ExaminationDTO dto);

    // Récupérer tous les examens médicaux
    List<ExaminationDTO> getAllExaminations();

    // Récupérer les examens d'un dossier médical
    List<ExaminationDTO> getExaminationsByRecordId(Long recordId);
}