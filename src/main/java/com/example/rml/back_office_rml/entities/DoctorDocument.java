package com.example.rml.back_office_rml.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Le lien avec le médecin
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    // Chemin ou URL du document justificatif
    @Column(nullable = false)
    private String documentUrl;

    // Photo du médecin
    @Column(nullable = true)
    private String photo;
}
