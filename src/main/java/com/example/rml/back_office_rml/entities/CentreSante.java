package com.example.rml.back_office_rml.entities;

import jakarta.persistence.*;

@Entity
public class CentreSante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_centre;

    @OneToOne
    @JoinColumn(name = "id_utilisateur", nullable = false)
    private Utilisateur utilisateur;

    @Column(nullable = false)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String adresse;

    @Column(name = "email_contact")
    private String emailContact;

    @Column(name = "telephone_contact")
    private String telephoneContact;


}
