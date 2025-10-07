package com.example.rml.back_office_rml.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientDTO {

    @NotBlank(message = "Le Prénom est obligatoire")
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    private String lastName;

    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    private String phoneNumber;

    @NotBlank
    @Email
    private String email;

    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long patientId;
}