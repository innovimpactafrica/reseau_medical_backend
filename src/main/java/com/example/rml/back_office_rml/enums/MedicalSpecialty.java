package com.example.rml.back_office_rml.enums;

import com.example.rml.back_office_rml.entities.Doctor;
import com.example.rml.back_office_rml.entities.Room;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

// Enum pour représenter les différentes spécialités médicales
@Getter
public enum MedicalSpecialty {

    GENERAL_MEDICINE("Médecine Générale"),
    CARDIOLOGY("Cardiologie"),
    DERMATOLOGY("Dermatologie"),
    PEDIATRICS("Pédiatrie"),
    GYNECOLOGY("Gynécologie"),
    ORTHOPEDICS("Orthopédie"),
    NEUROLOGY("Neurologie"),
    PSYCHIATRY("Psychiatrie"),
    OPHTHALMOLOGY("Ophtalmologie"),
    ENT("Oto-Rhino-Laryngologie"), // ORL
    PULMONOLOGY("Pneumologie"),
    GASTROENTEROLOGY("Gastroentérologie"),
    UROLOGY("Urologie"),
    RHEUMATOLOGY("Rhumatologie"),
    ENDOCRINOLOGY("Endocrinologie"),
    ONCOLOGY("Oncologie"),
    ANESTHESIA("Anesthésie-Réanimation"),
    RADIOLOGY("Radiologie"),
    GENERAL_SURGERY("Chirurgie Générale"),
    INTERNAL_MEDICINE("Médecine Interne"),
    NEPHROLOGY("Néphrologie"),
    HEMATOLOGY("Hématologie"),
    INFECTIOUS_DISEASES("Infectiologie"),
    IMMUNOLOGY("Immunologie"),
    ALLERGOLOGY("Allergologie"),
    NUCLEAR_MEDICINE("Médecine Nucléaire"),
    SPORTS_MEDICINE("Médecine du Sport"),
    OCCUPATIONAL_MEDICINE("Médecine du Travail"),
    EMERGENCY_MEDICINE("Médecine d’Urgence"),
    OTHER("Autre"); // Spécialité générique

    // getter pour récupérer le libellé
    private final String label; // libellé

    MedicalSpecialty(String label) {
        this.label = label;
    }

    /**
     * Représente une AFFECTATION d'un médecin à une salle
     * Le centre affecte un médecin à une salle en respectant :
     * - Les disponibilités déclarées du médecin
     * - Les horaires de la salle
     * - L'absence de conflits
     */
    @Entity
    @Table(name = "slots")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Slot {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "slot_id")
        private Long slotId;

        @Enumerated(EnumType.STRING)
        @Column(name = "day_of_week", nullable = false)
        private DayOfWeek dayOfWeek;

        @Column(name = "start_time", nullable = false)
        private LocalTime startTime;

        @Column(name = "end_time", nullable = false)
        private LocalTime endTime;

        @Enumerated(EnumType.STRING)
        @Column(name = "status", nullable = false)
        private SlotStatus status = SlotStatus.AVAILABLE;

        @Column(name = "is_recurring")
        private Boolean isRecurring = true;

        @Column(name = "created_at", nullable = false)
        private LocalDateTime createdAt;

        @Column(name = "updated_at")
        private LocalDateTime updatedAt;

        // Relations
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "doctor_id", nullable = false)
        private Doctor doctor;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "room_id", nullable = false)
        private Room room;

        @PrePersist
        protected void onCreate() {
            createdAt = LocalDateTime.now();
            updatedAt = LocalDateTime.now();
        }

        @PreUpdate
        protected void onUpdate() {
            updatedAt = LocalDateTime.now();
        }
    }
}
