package com.example.rml.back_office_rml.entities;


import com.example.rml.back_office_rml.enums.RoleUtilisateur;
import com.example.rml.back_office_rml.enums.StatutUtilisateur;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Utilisateur {

    @Id @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id_utilisateur;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "mot_de_passe")
    private String motDePasse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleUtilisateur role;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatutUtilisateur statut;

    @CreationTimestamp
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;


    // Relation
    @OneToOne(mappedBy = "utilisateur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Medecin medecin;

    @OneToOne(mappedBy = "utilisateur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CentreSante centreSante;


}
