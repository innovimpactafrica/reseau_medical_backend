package com.example.rml.back_office_rml.entities;

import com.example.rml.back_office_rml.enums.ReportType;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


//Compte rendu de consultation (texte, audio, vidéo).
@Entity
@Table(name = "consultation_reports")
public class ConsultationReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId; // Identifiant unique du compte rendu

    @ManyToOne
    @JoinColumn(name = "record_id")
    private MedicalRecord medicalRecord; // Lien vers le dossier médical principal du patient

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor; // Médecin qui a rédigé le compte rendu

    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment; // Rendez-vous associé à ce compte rendu

    @Column(name = "report_date")
    private LocalDate reportDate; // Date de rédaction du compte rendu

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ReportType type; // Format du compte rendu: TEXT, AUDIO, VIDEO

    @Column(name = "title")
    private String title; // Titre du compte rendu

    @Column(name = "category")
    private String category; // Catégorie: Consultation, Analyse, Radio, Urgence, etc.

    @Column(name = "content", columnDefinition = "TEXT")
    private String content; // Contenu textuel détaillé du compte rendu

    @Column(name = "file_path")
    private String filePath; // Chemin vers fichier audio/vidéo pour les comptes rendus multimédias

    @Column(name = "is_modifiable")
    private Boolean isModifiable = true; // Si le compte rendu peut être modifié après création

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
