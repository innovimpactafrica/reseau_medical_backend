package com.example.rml.back_office_rml.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthCenterDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Lien vers le centre de santé
    @ManyToOne
    @JoinColumn(name = "center_id", nullable = false)
    private HealthCenter healthCenter;

    // URL du logo
    @Column(nullable = true)
    private String logoUrl;

    // URLs des justificatifs (plusieurs, concaténés par virgule)
    @Column(nullable = true, columnDefinition = "TEXT")
    private String documentUrl;
}
