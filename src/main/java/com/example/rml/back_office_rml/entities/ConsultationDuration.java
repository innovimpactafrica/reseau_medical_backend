package com.example.rml.back_office_rml.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "consultation_durations")
public class ConsultationDuration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer minutes;           // 15, 20, 30, 45, 60

    @Column(nullable = false)
    private String displayName;        // "15 mins", "30 mins", "1 hour"

    @Column(nullable = false)
    private Boolean active = true;     // Pour désactiver certaines durées

}