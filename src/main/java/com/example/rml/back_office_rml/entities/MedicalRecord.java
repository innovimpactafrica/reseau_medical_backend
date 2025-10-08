package com.example.rml.back_office_rml.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 *  Classe représentant le dossier médical principal d'un patient
 *
 * Chaque patient possède un MedicalRecord unique qui regroupe toutes ses informations de santé :
 * - Antécédents médicaux
 * - Antécédents familiaux
 * - Examens médicaux
 * - Comptes rendus de consultation
 * - Ordonnances
 * - Dossiers partagés avec d'autres médecins
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "medical_records")
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //Identifiant unique du dossier médical


    @OneToOne
    @JoinColumn(name = "patient_id", unique = true)
    private Patient patient; //  Relation OneToOne avec le patient,  Chaque patient a exactement un dossier médical


    @Column(name = "record_number", unique = true)
    private String recordNumber;//  Numéro unique du dossier médical (peut être utilisé pour référencer le dossier)


    @Column(name = "blood_type")
    private String bloodType; // Groupe sanguin du patient

    @Column(name = "allergies", columnDefinition = "TEXT")
    private String allergies; // Allergies connues du patient


    @Column(name = "chronic_diseases", columnDefinition = "TEXT")
    private String chronicDiseases; // Maladies chroniques du patient


    @Column(name = "current_medications", columnDefinition = "TEXT")
    private String currentMedications; //  Médicaments actuels pris par le patient

    // ===========================================================
    // Relations avec les autres entités du dossier médical
    // ===========================================================

    // Liste des antécédents médicaux (ex: maladies passées, opérations)
    @OneToMany(mappedBy = "medicalRecord")
    private List<MedicalHistory> medicalHistories;

    // Liste des antécédents familiaux
    @OneToMany(mappedBy = "medicalRecord")
    private List<FamilyHistory> familyHistories;

    // Liste des examens médicaux (analyses, radiographies, etc.)
    @OneToMany(mappedBy = "medicalRecord")
    private List<Examination> examinations;

    // Liste des comptes rendus de consultation (texte, audio, vidéo)
    @OneToMany(mappedBy = "medicalRecord")
    private List<ConsultationReport> consultationReports;

    // Liste des prescriptions ou ordonnances
    @OneToMany(mappedBy = "medicalRecord")
    private List<Prescription> prescriptions;

    // Liste des fichiers partagés avec d'autres médecins
    @OneToMany(mappedBy = "medicalRecord")
    private List<SharedFile> sharedFiles;

    // ===========================================================
    //  Dates de création et mise à jour du dossier
    // ===========================================================
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Méthode exécutée avant l'insertion en base
     * Initialise les dates de création et modification
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Méthode exécutée avant la mise à jour en base
     * Met à jour la date de modification
     */
    @PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();
    }

}
