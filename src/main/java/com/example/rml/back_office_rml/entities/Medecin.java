package com.example.rml.back_office_rml.entities;


import com.example.rml.back_office_rml.enums.SpecialiteMedicale;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpecialiteMedicale specialite;

    @Column(nullable = false)
    private String telephone;

    // Stockage des fichiers en base de données sous forme de byte[]
    @Lob
    @Column(name = "photo" , columnDefinition = "LONGBLOB")
    private byte[] photo;

    @Lob
    @Column(name = "justificatifs", columnDefinition = "LONGBLOB")
    private byte[] justificatifs;


}
