package com.example.rml.back_office_rml.entities;

import com.example.rml.back_office_rml.enums.DayOfWeek;
import com.example.rml.back_office_rml.enums.SlotStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "slots")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Slot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "slot_id")
    private Long slotId;

  
    @Column(name = "slot_date")
    private LocalDate slotDate;

    // JOUR DE LA SEMAINE (pour créneaux récurrents)
    @Enumerated(EnumType.STRING) // Stocke l'enum comme texte (MONDAY, TUESDAY...)
    @Column(name = "day_of_week")
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time", nullable = false) // Heure de début
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false) // Heure de fin
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false) // Statut du slot (AVAILABLE, OCCUPIED...)
    private SlotStatus status = SlotStatus.AVAILABLE;

    // Indique si le créneau est récurrent
    @Column(name = "is_recurring")
    private Boolean isRecurring = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY) // Relation vers le médecin
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY) // Relation vers la salle
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    // Avant insertion en base
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        // Si dayOfWeek non renseigné, on le calcule à partir de slotDate pour les vérifications partie repository
        if (slotDate != null && dayOfWeek == null) {
            dayOfWeek = convertToDayOfWeek(slotDate.getDayOfWeek());
        }
    }

    // Avant mise à jour en base
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Conversion du DayOfWeek Java standard vers notre enum personnalisé
    private DayOfWeek convertToDayOfWeek(java.time.DayOfWeek javaDayOfWeek) {
        return switch (javaDayOfWeek) {
            case MONDAY -> DayOfWeek.MONDAY;
            case TUESDAY -> DayOfWeek.TUESDAY;
            case WEDNESDAY -> DayOfWeek.WEDNESDAY;
            case THURSDAY -> DayOfWeek.THURSDAY;
            case FRIDAY -> DayOfWeek.FRIDAY;
            case SATURDAY -> DayOfWeek.SATURDAY;
            case SUNDAY -> DayOfWeek.SUNDAY;
        };
    }
}
