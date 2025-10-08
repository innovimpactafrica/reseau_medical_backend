package com.example.rml.back_office_rml.entities;

import com.example.rml.back_office_rml.enums.FamilyRelation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

//Maladies héréditaires dans la famille.
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "family_histories")
public class FamilyHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "record_id")
    private MedicalRecord medicalRecord; //plusieurs antécédents familiaux peuvent appartenir au même dossier médical

    @Enumerated(EnumType.STRING)
    @Column(name = "relation")
    private FamilyRelation relation; // FATHER, MOTHER etc.

    @Column(name = "age")
    private Integer age; //Âge du membre de la famille.

    @Column(name = "medical_condition")
    private String condition; // Hypertension, Diabète, Cancer, etc.

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes; //Notes supplémentaires sur le membre de la famille.


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