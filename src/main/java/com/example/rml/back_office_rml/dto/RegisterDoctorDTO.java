package com.example.rml.back_office_rml.dto;

import com.example.rml.back_office_rml.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.example.rml.back_office_rml.enums.MedicalSpecialty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;


@Data
@Component
public class RegisterDoctorDTO {



    // User information (REQUIRED for all)
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8)
    private String password; //

    // Doctor-specific information
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne peut dépasser 100 caractères")
    private String lastName; // nom

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100, message = "Le prénom ne peut dépasser 100 caractères")
    private String firstName; // prenom

    @NotNull(message = "La spécialité est obligatoire")
    private MedicalSpecialty specialty; // specialite

    @NotBlank(message = "Le téléphone est obligatoire")
    @Size(max = 15, message = "Le téléphone ne peut dépasser 15 caractères")
    private String phone; // telephone

    // Lors du retour du DTO Doctor, les fichiers ne sont pas affichés car les données en byte ne sont pas converties en Multipart. On utilise à la place des indicateurs de présence.
    @JsonIgnore
    private MultipartFile photo;

    @JsonIgnore
    private MultipartFile documents; // justificatifs

    @Schema(hidden = true)
    private UserStatus status;

    private LocalDateTime creationDate;


    // File presence indicators
    private boolean hasPhoto = false;
    private boolean hasDocuments = false;

}
