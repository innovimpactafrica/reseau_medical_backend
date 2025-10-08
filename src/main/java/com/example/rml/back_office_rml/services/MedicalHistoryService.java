package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.MedicalHistoryDTO;

import java.util.List;

// ========================================
// SERVICE POUR ANTÉCÉDENTS MÉDICAUX
// ========================================
public interface MedicalHistoryService {

    // Ajouter un antécédent médical
    MedicalHistoryDTO addMedicalHistory(MedicalHistoryDTO dto);

    // Mettre à jour un antécédent médical
    MedicalHistoryDTO updateMedicalHistory(Long historyId, MedicalHistoryDTO dto);

    // Récupérer tous les antécédents médicaux
    List<MedicalHistoryDTO> getAllMedicalHistories();

    // Récupérer les antécédents d'un dossier médical
    List<MedicalHistoryDTO> getMedicalHistoriesByRecordId(Long recordId);
}