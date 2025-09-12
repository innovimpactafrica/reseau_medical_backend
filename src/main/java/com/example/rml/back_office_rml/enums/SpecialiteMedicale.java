package com.example.rml.back_office_rml.enums;

// Enum pour représenter les différentes spécialités médicales
public enum SpecialiteMedicale {

    // Liste des constantes de l'enum avec leur libellé (valeur descriptive)
    MEDECINE_GENERALE("Médecine Générale"),
    CARDIOLOGIE("Cardiologie"),
    DERMATOLOGIE("Dermatologie"),
    PEDIATRIE("Pédiatrie"),
    GYNECOLOGIE("Gynécologie"),
    ORTHOPEDIE("Orthopédie"),
    NEUROLOGIE("Neurologie"),
    PSYCHIATRIE("Psychiatrie"),
    OPHTALMOLOGIE("Ophtalmologie"),
    ORL("Oto-Rhino-Laryngologie"),
    PNEUMOLOGIE("Pneumologie"),
    GASTROENTEROLOGIE("Gastroentérologie"),
    UROLOGIE("Urologie"),
    RHUMATOLOGIE("Rhumatologie"),
    ENDOCRINOLOGIE("Endocrinologie"),
    ONCOLOGIE("Oncologie"),
    ANESTHESIE("Anesthésie-Réanimation"),
    RADIOLOGIE("Radiologie"),
    CHIRURGIE_GENERALE("Chirurgie Générale"),
    MEDECINE_INTERNE("Médecine Interne"),
    NEPHROLOGIE("Néphrologie"),
    HEMATOLOGIE("Hématologie"),
    INFECTIOLOGIE("Infectiologie"),
    IMMUNOLOGIE("Immunologie"),
    ALLERGOLOGIE("Allergologie"),
    MEDECINE_NUCLEAIRE("Médecine Nucléaire"),
    MEDECINE_SPORT("Médecine du Sport"),
    MEDECINE_TRAVAIL("Médecine du Travail"),
    URGENCES("Médecine d’Urgence"),
    AUTRE("Autre"); // Valeur générique pour les spécialités non listées

    // Attribut qui stocke le libellé associé à la constante
    private final String libelle;

    // Constructeur de l'enum : appelé automatiquement pour chaque constante
    SpecialiteMedicale(String libelle) {

        this.libelle = libelle; // On stocke le libellé passé en paramètre
    }

    // Getter pour récupérer le libellé d'une spécialité
    public String getLibelle() {
        return libelle;
    }
}
