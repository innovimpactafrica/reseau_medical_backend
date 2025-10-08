package com.example.rml.back_office_rml.dto;

import com.example.rml.back_office_rml.enums.FamilyRelation;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO pour les antécédents familiaux
 * Maladies héréditaires dans la famille du patient
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FamilyHistoryDTO {

    // ID de l'antécédent familial (auto-généré)
    private Long id;

    // ID du dossier médical associé (obligatoire)
    @NotNull(message = "L'ID du dossier médical est obligatoire")
    private Long recordId;

    // Lien de parenté (obligatoire) - FATHER, MOTHER, BROTHER, etc.
    @NotNull(message = "Le lien de parenté est obligatoire")
    private FamilyRelation relation;

    // Âge du membre de la famille (facultatif)
    private Integer age;

    // Condition médicale (obligatoire) - ex: Diabète, Cancer
    @NotBlank(message = "La condition médicale est obligatoire")
    private String condition;

    // Notes supplémentaires (facultatif)
    private String notes;

    // Dates de création et modification
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;
}