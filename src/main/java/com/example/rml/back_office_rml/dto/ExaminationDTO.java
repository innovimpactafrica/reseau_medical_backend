package com.example.rml.back_office_rml.dto;

import com.example.rml.back_office_rml.enums.ExaminationType;
import com.example.rml.back_office_rml.enums.MedicalSpecialty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pour les examens médicaux
 * Analyses, radiographies, tests, etc.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExaminationDTO {

    // ID de l'examen (auto-généré)
    private Long id;

    // ID du dossier médical associé (obligatoire)
    @NotNull(message = "L'ID du dossier médical est obligatoire")
    private Long recordId;

    // ID du médecin qui a prescrit l'examen (obligatoire)
    @NotNull(message = "L'ID du médecin est obligatoire")
    private Long doctorId;

    // Date de l'examen (obligatoire)
    @NotNull(message = "La date de l'examen est obligatoire")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate examinationDate;

    // Type d'examen (obligatoire) - BLOOD_TEST, XRAY, MRI, etc.
    @NotNull(message = "Le type d'examen est obligatoire")
    private ExaminationType type;

    // Titre de l'examen (obligatoire)
    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    // Résultats de l'examen (facultatif)
    private String results;

    // Notes du médecin (facultatif)
    private String notes;

    // Chemin du fichier (PDF, image) - facultatif
    private List <String> resultFiles;

    // Informations du médecin (pour affichage)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String doctorFirstName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String doctorLastName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private MedicalSpecialty doctorSpecialty;

    // Dates de création et modification
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;;
}