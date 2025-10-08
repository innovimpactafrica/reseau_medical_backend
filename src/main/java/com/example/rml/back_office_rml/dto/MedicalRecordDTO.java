package com.example.rml.back_office_rml.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO pour le dossier médical d'un patient
 * Contient les informations principales du dossier médical
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordDTO {

    // ID du dossier médical (auto-généré)
    private Long id;

    // ID du patient propriétaire du dossier (obligatoire)
    @NotNull(message = "L'ID du patient est obligatoire")
    private Long patientId;

    // Numéro unique du dossier (généré automatiquement)
    private String recordNumber;

    // Groupe sanguin (ex: A+, O-, AB+)
    private String bloodType;

    // Allergies connues (texte libre)
    private String allergies;

    // Maladies chroniques (ex: diabète, hypertension)
    private String chronicDiseases;

    // Médicaments actuellement pris
    private String currentMedications;

    // Informations du patient (nom, prénom) - pour affichage uniquement
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String patientFirstName;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String patientLastName;

    // Dates de création et modification
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;
}