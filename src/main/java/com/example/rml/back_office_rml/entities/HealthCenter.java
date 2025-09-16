package com.example.rml.back_office_rml.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class HealthCenter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long centerId; // ID du centre

    @OneToOne
    @JoinColumn(name = "id_utilisateur", referencedColumnName = "id_utilisateur", nullable = false)
    private Users user;

    @Column(nullable = false)
    private String name; // nom du centre

    @Column(columnDefinition = "TEXT")
    private String address; // adresse du centre

    @Column(nullable = false)
    private String openingHours; // horaires d'ouverture

    @Column(nullable = false)
    private String contactPerson; // personne de contact

    @Column(unique = true, name = "telephone_contact")
    private String contactPhone; // téléphone de contact

    @Lob
    @Column(name = "logo", columnDefinition = "LONGBLOB")
    private byte[] logo; // Logo du centre

    @Lob
    @Column(name = "justificatifs", columnDefinition = "LONGBLOB")
    private byte[] documents; // documents justificatifs

    // GETTER pour le frontend (sans le mot de passe)
    public String getPassword() {
        return ""; //
    }
}
