package com.example.rml.back_office_rml.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "prescription_items")
public class PrescriptionItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identifiant unique de l'élément de prescription

    @ManyToOne
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;// Plusieurs PrescriptionItem peuvent appartenir à une Prescription unique.

    @Column(name = "medication_name")
    private String medicationName; // Nom du médicament

    @Column(name = "dosage")
    private String dosage; // Exemple : "500mg"

    @Column(name = "frequency")
    private String frequency; // Exemple : "2 fois par jour"

    @Column(name = "duration")
    private String duration; // Exemple : "10 jours"

    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions; // Instructions détaillées (texte long)
}
