package com.example.rml.back_office_rml.entities;

import com.example.rml.back_office_rml.enums.PrescriptionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "prescriptions")
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identifiant unique de l'ordonnance

    @ManyToOne
    @JoinColumn(name = "record_id")
    private MedicalRecord medicalRecord; // Lien vers le dossier médical du patient

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor; // Médecin prescripteur

    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment; // Rendez-vous ayant généré cette ordonnance

    @Column(name = "prescription_date")
    private LocalDate prescriptionDate; // Date de prescription

    @Column(name = "prescription_number", unique = true)
    private String prescriptionNumber; // Numéro unique d'ordonnance

    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL)
    private List<PrescriptionItem> items; // Liste des médicaments/soins prescrits

    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions; // Instructions générales et recommandations

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PrescriptionStatus status; // Statut: ACTIVE, EXPIRED, CANCELLED (Statut EXPIRED : Après la date validUntil)

    @Column(name = "valid_until")
    private LocalDate validUntil; // Date de validité de l'ordonnance

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