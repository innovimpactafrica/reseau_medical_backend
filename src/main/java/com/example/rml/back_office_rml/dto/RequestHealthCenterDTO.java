package com.example.rml.back_office_rml.dto;

import com.example.rml.back_office_rml.enums.UserStatus;
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
    private LocalDateTime creationDate; // Date de création de la demande

    private boolean hasLogo; // Indique si le centre a un logo enregistré
    private boolean hasCenterDocuments; // Indique si des documents sont associés au centre

}
