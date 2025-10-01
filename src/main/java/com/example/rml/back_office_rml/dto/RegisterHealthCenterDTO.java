package com.example.rml.back_office_rml.dto;

import com.example.rml.back_office_rml.enums.UserRole;
import com.example.rml.back_office_rml.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;


@Component
@Data
public class RegisterHealthCenterDTO {

    // User information (REQUIRED for all)
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8)
    private String password;// password

    @NotNull
    private UserRole role;// role

    // Specific information for HEALTH CENTER
    @NotBlank(message = "The health center name is required")
    @Size(max = 200, message = "The health center name cannot exceed 200 characters")
    private String healthCenterName;

    @Size(max = 500, message = "The address cannot exceed 500 characters")
    private String healthCenterAddress;

    private String openingHours;

    @NotBlank(message = "The referent's name is required")
    @Size(max = 100, message = "The referent's name cannot exceed 100 characters")
    private String referentName;

    @Size(max = 15, message = "The phone number cannot exceed 15 characters")
    private String referentPhone;

    @Schema(hidden = true)
    private UserStatus status;


    // Lors du retour du DTO HealthCenter les fichiers ne sont pas affichés car les données en byte ne sont pas converties en Multipart. On utilise à la place des indicateurs de présence.
    @JsonIgnore
    private MultipartFile logo;

    @JsonIgnore
    private MultipartFile documents; // justificatifs

    // File presence indicators
    private boolean hasLogo = false;
    private boolean hasDocuments = false;


}
