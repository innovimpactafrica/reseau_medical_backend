package com.example.rml.back_office_rml.entities;

import com.example.rml.back_office_rml.enums.ShareStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "shared_files")
public class SharedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // ID unique de chaque partage


    @ManyToOne
    @JoinColumn(name = "record_id")  // Plusieurs SharedFile peuvent appartenir à un MedicalRecord
    private MedicalRecord medicalRecord;  // Le dossier médical partagé


    @ManyToOne
    @JoinColumn(name = "shared_by_doctor_id")
    private Doctor sharedByDoctor;  // Le médecin qui partage le dossier


    @ManyToOne
    @JoinColumn(name = "shared_with_doctor_id")
    private Doctor sharedWithDoctor;  // Le médecin avec qui le dossier est partagé

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;  // Message d'accompagnement du partage

    @Enumerated(EnumType.STRING)  // Stocke le nom de l'enum en texte (ex: "PENDING")
    @Column(name = "status")
    private ShareStatus status;  // État du partage : PENDING, ACCEPTED, DECLINED

    @Column(name = "shared_at")
    private LocalDateTime sharedAt;  // Date/heure où le partage a été initié

    // Date et heure de la dernière modification
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Méthode exécutée avant l'insertion en base
     * Initialise les dates de création et modification
     */
    @PrePersist
    protected void onCreate() {
        sharedAt = LocalDateTime.now();
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