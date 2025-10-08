package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.*;
import java.util.List;

// ========================================
// SERVICE POUR DOSSIER MÉDICAL
// ========================================
public interface MedicalRecordService {

    // Créer un nouveau dossier médical pour un patient
    MedicalRecordDTO createMedicalRecord(MedicalRecordDTO dto);

    // Mettre à jour un dossier médical existant
    MedicalRecordDTO updateMedicalRecord(Long recordId, MedicalRecordDTO dto);

    // Récupérer tous les dossiers médicaux
    List<MedicalRecordDTO> getAllMedicalRecords();

    // Récupérer le dossier médical d'un patient par son ID
    MedicalRecordDTO getMedicalRecordByPatientId(Long patientId);

    // Récupérer un dossier médical par son Numéro
    MedicalRecordDTO getMedicalRecordByRecordNumber(String recordNumber);
}