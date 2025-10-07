package com.example.rml.back_office_rml.entities;

import com.example.rml.back_office_rml.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appointmentId;

    // Relation avec le patient (Many appointments can belong to one patient)
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    //Relation OneToOne avec le Créneau (slot)
    @OneToOne
    @JoinColumn(name = "slot_id", unique = true)
    private Slot slot;

    // Raison de la consultation
    @Column(name = "consultation_reason", length = 255)
    private String consultationReason;

    //Date de création  et de la modification du rendez-vous
    @Column(name = "created_at", nullable = false)
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

    // Statut du rendez-vous
    @Enumerated(EnumType.STRING)
    @Column( nullable = false)
    private AppointmentStatus status = AppointmentStatus.PENDING;
}
