package com.example.rml.back_office_rml.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO pour les antécédents médicaux
 * Représente une maladie ou intervention passée du patient
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalHistoryDTO {

    // ID de l'antécédent (auto-généré)
    private Long id;

    // ID du dossier médical associé (obligatoire)
    @NotNull(message = "L'ID du dossier médical est obligatoire")
    private Long recordId;

    // Date de l'événement médical (obligatoire)
    @JsonFormat(pattern = "dd-MM-yyyy")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate date;

    // Diagnostic principal (obligatoire)
    @NotBlank(message = "Le diagnostic est obligatoire")
    private String diagnosis;

    // Description détaillée (facultatif)
    private String description;

    // Dates de création et modification
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;
}