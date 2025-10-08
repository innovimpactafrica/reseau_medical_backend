package com.example.rml.back_office_rml.dto;

import com.example.rml.back_office_rml.enums.MedicalSpecialty;
import com.example.rml.back_office_rml.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestDoctorDTO {


    private Long userId;       // ID de l'utilisateur

    private Long doctorId;     // ID du médecin

    private String lastName;   // Nom du médecin
    private String firstName;  // Prénom du médecin
    private String email;      // Email du médecin

    private UserStatus status; // Statut de l'utilisateur

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime dateOfRequest; // dateCreation

    private MedicalSpecialty specialty; // Spécialité médicale
    private String phone;               // Numéro de téléphone

}
