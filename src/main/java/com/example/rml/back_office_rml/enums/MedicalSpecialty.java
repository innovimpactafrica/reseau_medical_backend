package com.example.rml.back_office_rml.enums;

import lombok.Getter;

// Enum pour représenter les différentes spécialités médicales
@Getter
public enum MedicalSpecialty {

    GENERAL_MEDICINE("Médecine Générale"),
    CARDIOLOGY("Cardiologie"),
    DERMATOLOGY("Dermatologie"),
    PEDIATRICS("Pédiatrie"),
    GYNECOLOGY("Gynécologie"),
    ORTHOPEDICS("Orthopédie"),
    NEUROLOGY("Neurologie"),
    PSYCHIATRY("Psychiatrie"),
    OPHTHALMOLOGY("Ophtalmologie"),
    ENT("Oto-Rhino-Laryngologie"), // ORL
    PULMONOLOGY("Pneumologie"),
    GASTROENTEROLOGY("Gastroentérologie"),
    UROLOGY("Urologie"),
    RHEUMATOLOGY("Rhumatologie"),
    ENDOCRINOLOGY("Endocrinologie"),
    ONCOLOGY("Oncologie"),
    ANESTHESIA("Anesthésie-Réanimation"),
    RADIOLOGY("Radiologie"),
    GENERAL_SURGERY("Chirurgie Générale"),
    INTERNAL_MEDICINE("Médecine Interne"),
    NEPHROLOGY("Néphrologie"),
    HEMATOLOGY("Hématologie"),
    INFECTIOUS_DISEASES("Infectiologie"),
    IMMUNOLOGY("Immunologie"),
    ALLERGOLOGY("Allergologie"),
    NUCLEAR_MEDICINE("Médecine Nucléaire"),
    SPORTS_MEDICINE("Médecine du Sport"),
    OCCUPATIONAL_MEDICINE("Médecine du Travail"),
    EMERGENCY_MEDICINE("Médecine d’Urgence"),
    OTHER("Autre"); // Spécialité générique

    // getter pour récupérer le libellé
    private final String label; // libellé

    MedicalSpecialty(String label) {
        this.label = label;
    }

}
