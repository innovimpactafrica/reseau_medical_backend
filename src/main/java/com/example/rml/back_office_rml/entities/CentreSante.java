package com.example.rml.back_office_rml.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class CentreSante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_centre;

    @OneToOne
    @JoinColumn(name = "id_utilisateur", nullable = false)
    private Utilisateur utilisateur;

    @Column(nullable = false)
    private String nomCentre;


    @Column(columnDefinition = "TEXT")
    private String adresseCentre;

    @Column (nullable = false)
    private String horaireOuverture;

    @Column(nullable = false)
    private String nomReferent;

    @Column(name = "telephone_contact")
    private String telephoneReferent;


}
