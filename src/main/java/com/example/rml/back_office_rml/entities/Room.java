package com.example.rml.back_office_rml.entities;

import com.example.rml.back_office_rml.enums.RoomStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // identifiant unique

    @Column(nullable = false)
    private String name; // nom ou numéro de la salle

    @Positive(message = "Capacity must be positive")
    private Integer capacity; // capacité de la salle (optionnel)


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus status = RoomStatus.AVAILABLE; // statut par défaut

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "center_id", nullable = false)
    private HealthCenter healthCenter; // Centre propriétaire de la salle

    @Column(name = "created_at", nullable = false)
    private java.time.LocalDateTime createdAt; // Date de création

    @Column(name = "updated_at", nullable = false)
    private java.time.LocalDateTime updatedAt; // Date de mise à jour

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        updatedAt = java.time.LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = java.time.LocalDateTime.now();
    }
}
