package com.example.rml.back_office_rml.entities;


import com.example.rml.back_office_rml.enums.SpecialiteMedicale;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

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

    @Lob
    @Column(name = "photo")
    private byte[] photo;

    @Lob
    @Column(name = "justificatifs")
    private byte [] justificatifs;


}
