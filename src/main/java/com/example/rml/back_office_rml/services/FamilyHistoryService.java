package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.FamilyHistoryDTO;

import java.util.List;

// ========================================
// SERVICE POUR ANTÉCÉDENTS FAMILIAUX
// ========================================
public interface FamilyHistoryService {

    // Ajouter un antécédent familial
    FamilyHistoryDTO addFamilyHistory(FamilyHistoryDTO dto);

    // Mettre à jour un antécédent familial
    FamilyHistoryDTO updateFamilyHistory(Long familyHistoryId, FamilyHistoryDTO dto);

    // Récupérer tous les antécédents familiaux
    List<FamilyHistoryDTO> getAllFamilyHistories();

    // Récupérer les antécédents familiaux d'un dossier médical
    List<FamilyHistoryDTO> getFamilyHistoriesByRecordId(Long recordId);
}