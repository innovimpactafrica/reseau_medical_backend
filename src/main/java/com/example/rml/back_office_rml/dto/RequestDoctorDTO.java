package com.example.rml.back_office_rml.dto;

import com.example.rml.back_office_rml.enums.MedicalSpecialty;
import com.example.rml.back_office_rml.enums.UserStatus;
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
    private LocalDateTime creationDate; // Date de création

    private MedicalSpecialty specialty; // Spécialité médicale
    private String phone;               // Numéro de téléphone
    private boolean hasPhoto;           // Indique si une photo est disponible
    private boolean hasDocuments;       // Indique si des documents sont disponibles
}
