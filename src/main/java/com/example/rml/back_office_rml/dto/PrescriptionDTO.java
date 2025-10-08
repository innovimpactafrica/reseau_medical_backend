package com.example.rml.back_office_rml.dto;

import com.example.rml.back_office_rml.enums.PrescriptionStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO pour les ordonnances médicales
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionDTO {

    // ID de l'ordonnance (auto-généré)
    private Long id;

    // ID du dossier médical associé (obligatoire)
    @NotNull(message = "L'ID du dossier médical est obligatoire")
    private Long recordId;

    // ID du médecin prescripteur (obligatoire)
    @NotNull(message = "L'ID du médecin est obligatoire")
    private Long doctorId;

    // ID du rendez-vous associé (facultatif)
    private Long appointmentId;

    // Date de prescription (obligatoire)
    @NotNull(message = "La date de prescription est obligatoire")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate prescriptionDate;

    // Numéro unique de l'ordonnance (généré automatiquement)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String prescriptionNumber;

    // Liste des médicaments prescrits
    private List<PrescriptionItemDTO> items = new ArrayList<>();

    // Instructions générales pour le patient
    private String instructions;

    // Statut de l'ordonnance
    private PrescriptionStatus status;

    // Date de validité de l'ordonnance
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate validUntil;

    // Informations du médecin (pour affichage)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String doctorFirstName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String doctorLastName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String doctorSpecialty;

    // Informations du patient (pour affichage)
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