package com.example.rml.back_office_rml.enums;

    public enum SpecialiteMedicale {
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
        AUTRE("Autre");

        private final String libelle;

        SpecialiteMedicale(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }

}
