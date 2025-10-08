package com.example.rml.back_office_rml.dto;

import com.example.rml.back_office_rml.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestHealthCenterDTO {


    private Long userId; // ID de l'utilisateur lié au centre
    private Long centerId; // ID spécifique du centre

    private String name; // Nom du centre de santé
    private String email; // Email du centre
    private String address; // Adresse du centre
    private String openingHours; // Horaires d'ouverture

    private String contactPerson; // Nom de la personne de contact
    private String contactPhone;  // Téléphone de la personne de contact

    private UserStatus status; // Statut de la demande
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime dateOfRequest; // dateCreation



}
