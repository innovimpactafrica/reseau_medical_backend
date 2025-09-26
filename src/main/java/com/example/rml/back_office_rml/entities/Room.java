package com.example.rml.back_office_rml.entities;

import com.example.rml.back_office_rml.enums.DayOfWeek;
import com.example.rml.back_office_rml.enums.RoomStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Entité représentant une salle dans un centre de santé
 * Gère les informations de base d'une salle et ses créneaux horaires
 */
@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    // Identifiant unique de la salle
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrémenté par la base
    @Column(name = "room_id")
    private Long roomId;

    // Nom de la salle (ex: "Salle de consultation 1")
    @Column(name = "name", nullable = false)
    private String name;

    // Capacité maximale d'accueil de la salle
    @Column(name = "capacity")
    private Integer capacity;

    // Statut actuel de la salle (disponible, occupée, maintenance)
    @Enumerated(EnumType.STRING) // Stocke l'enum comme chaîne en base
    @Column(name = "status", nullable = false)
    private RoomStatus status = RoomStatus.AVAILABLE; // Valeur par défaut

    // Liste des jours de la semaine où la salle est disponible
    @ElementCollection(targetClass = DayOfWeek.class)
    @CollectionTable(name = "room_available_days", // Table pour stocker les jours disponibles
            joinColumns = @JoinColumn(name = "room_id")) // Clé étrangère vers Room
    @Column(name = "day")// Nom de la colonne qui stocke les jours
    @Enumerated(EnumType.STRING)// Stocke le nom du jour (ex: "MONDAY") plutôt qu'un nombre
    private Set<DayOfWeek> availableDays;// Set garantit l'unicité des jours

    // Centre de santé auquel appartient cette salle
    @ManyToOne(fetch = FetchType.LAZY) // Relation Many-to-One avec chargement lazy
    @JoinColumn(name = "health_center_id", nullable = false)
    private HealthCenter healthCenter;

    // Date et heure de création de l'enregistrement
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Date et heure de la dernière modification
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Liste des créneaux horaires par défaut pour cette salle
    @OneToMany(mappedBy = "room", // Relation inverse dans DefaultTimeSlot
            cascade = CascadeType.ALL, // Opérations cascadées aux créneaux
            orphanRemoval = true) // Supprime les créneaux sans salle
    private List<DefaultTimeSlot> defaultTimeSlots;

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