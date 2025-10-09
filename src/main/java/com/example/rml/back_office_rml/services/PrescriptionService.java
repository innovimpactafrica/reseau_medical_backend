package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.PrescriptionDTO;
import java.util.List;

public interface PrescriptionService {

    /**
     * Créer une nouvelle ordonnance
     */
    PrescriptionDTO createPrescription(PrescriptionDTO dto);

    /**
     * Modifier une ordonnance existante
     */
    PrescriptionDTO updatePrescription(Long prescriptionId, PrescriptionDTO dto);

    /**
     * Récupérer toutes les ordonnances
     */
    List<PrescriptionDTO> getAllPrescriptions();

    /**
     * Récupérer toutes les ordonnances d'un dossier médical
     */
    List<PrescriptionDTO> getPrescriptionsByRecordId(Long recordId);

    /**
     * Récupérer une ordonnance par son Numéro
     */
    PrescriptionDTO getPrescriptionByNumber(String prescriptionNumber);
}