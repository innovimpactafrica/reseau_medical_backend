package com.example.rml.back_office_rml.enums;

public enum SlotStatus {
    AVAILABLE,      // Disponible pour réservation
    RESERVED,       // Réservé
    CANCELLED,      // Annulé
    COMPLETED,      // Consultation terminée
    UNAVAILABLE,    // Créneau devenu inutilisable (expiré)
    EXPIRED         // Expiré , la date est passée
}
