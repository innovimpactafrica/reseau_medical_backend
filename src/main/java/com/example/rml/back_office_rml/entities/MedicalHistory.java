package com.example.rml.back_office_rml.entities;


import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


//Historique des maladies et interventions du patient
@Entity
@Table(name = "medical_histories")
public class MedicalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "record_id")
    private MedicalRecord medicalRecord; // relation avec le dossier Médical

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "diagnosis")
    private String diagnosis; //le diagnostic médical principal pour cet antécédent (ex: grippe, fracture, diabète).

    @Column(name = "description", columnDefinition = "TEXT")
    private String description; //détails supplémentaires

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
