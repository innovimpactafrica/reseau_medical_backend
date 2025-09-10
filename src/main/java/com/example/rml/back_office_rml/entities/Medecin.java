package com.example.rml.back_office_rml.entities;


import jakarta.persistence.*;

@Entity
public class Medecin {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id_medecin;

    @OneToOne
    @JoinColumn(name = "id_utilisateur", nullable = false)
    private Utilisateur utilisateur;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false)
    private String specialite;

    @Column(nullable = false)
    private String telephone;

    @Lob
    @Column(name = "photo")
    private byte[] photo;

    @Lob
    @Column(name = "justificatifs")
    private byte[] justificatifs;


}
