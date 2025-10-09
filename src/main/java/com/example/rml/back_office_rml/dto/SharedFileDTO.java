package com.example.rml.back_office_rml.dto;

import com.example.rml.back_office_rml.enums.ShareStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pour le partage de dossier médical entre médecins
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedFileDTO {



    @NotNull(message = "L'ID du dossier médical est obligatoire")
    private Long recordId;

    @NotNull(message = "L'ID du médecin qui partage est obligatoire")
    private Long sharedByDoctorId;

    @NotNull(message = "L'ID du médecin destinataire est obligatoire")
    private Long sharedWithDoctorId;

    // NOUVEAU : Liste des IDs des documents à partager
    // Peut contenir des IDs d'examens, ordonnances, comptes rendus
    private List<Long> sharedDocumentIds;

    // NOUVEAU : Types de documents partagés
    // Ex: ["EXAMINATION", "PRESCRIPTION", "CONSULTATION_REPORT"]
    private List<String> sharedDocumentTypes;

    private String comment;

    // Statut du partage : PENDING, ACCEPTED, DECLINED
    // PENDING par défaut car le médecin destinataire doit accepter
    private ShareStatus status;

    // Informations du médecin qui partage
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String sharedByDoctorFirstName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String sharedByDoctorLastName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String sharedByDoctorSpecialty;

    // Informations du médecin destinataire
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String sharedWithDoctorFirstName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String sharedWithDoctorLastName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String sharedWithDoctorSpecialty;

    // Informations du patient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String patientFirstName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String patientLastName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String recordNumber;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime sharedAt;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;
}