package com.example.rml.back_office_rml.entities;

import com.example.rml.back_office_rml.enums.ExaminationType;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

//Examens, analyses, résultats.
@Entity
@Table(name = "examinations")
public class Examination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "record_id")
    private MedicalRecord medicalRecord;//plusieurs examens peuvent appartenir au même dossier médical



    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor; //Le médecin qui a prescrit/analysé l'examen

    @Column(name = "examination_date")
    private LocalDate examinationDate; // Date de réalisation de l'examen

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ExaminationType type;

    @Column(name = "title")
    private String title;// Titre personnalisé de l'examen

    @Column(name = "results", columnDefinition = "TEXT")
    private String results;  // Résultats textuels (TEXT = long texte)

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;          // Commentaires/observations du médecin

    @Column(name = "file_path")
    private String filePath;       // Chemin vers le fichier (PDF, image)

    // ===========================================================
    //Dates de création et mise à jour du dossier
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
