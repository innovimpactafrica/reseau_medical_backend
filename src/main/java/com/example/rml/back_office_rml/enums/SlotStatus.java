package com.example.rml.back_office_rml.enums;

public enum SlotStatus {
    AVAILABLE,      // Disponible pour réservation
    RESERVED,       // Réservé par un centre de santé
    UNAVAILABLE,    // Indisponible
    CANCELLED,      // Annulé
    COMPLETED       // Consultation terminée
}