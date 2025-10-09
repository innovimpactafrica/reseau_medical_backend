package com.example.rml.back_office_rml.dto;

import com.example.rml.back_office_rml.enums.MedicalSpecialty;
import com.example.rml.back_office_rml.enums.ReportType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationReportDTO {


    @NotNull(message = "L'ID du dossier médical est obligatoire")
    private Long recordId;

    @NotNull(message = "L'ID du médecin est obligatoire")
    private Long doctorId;

    private Long appointmentId;

    @NotNull(message = "La date du rapport est obligatoire")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate reportDate;

    @NotNull(message = "Le type de compte rendu est obligatoire")
    private ReportType type;

    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    private String category;

    // Pour les comptes rendus TEXTE
    private String content;

    // Pour les comptes rendus AUDIO/VIDEO
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String filePath;

    private Boolean isModifiable;

    // Informations du médecin
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String doctorFirstName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String doctorLastName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private MedicalSpecialty doctorSpecialty;

    // Informations du patient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String patientFirstName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String patientLastName;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;
}