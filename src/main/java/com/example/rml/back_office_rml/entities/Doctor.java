package com.example.rml.back_office_rml.entities;

import com.example.rml.back_office_rml.enums.MedicalSpecialty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long doctorId; // ID du médecin

    @OneToOne
    @JoinColumn(name = "id_utilisateur", nullable = false , referencedColumnName = "id_utilisateur")
    private Users user; // lien avec l'utilisateur

    @Column(nullable = false)
    private String lastName; // nom

    @Column(nullable = false)
    private String firstName; // prénom

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MedicalSpecialty specialty; // spécialité médicale

    @Column(nullable = false)
    private String phone; // téléphone

    @Lob
    @Column(name = "photo", columnDefinition = "LONGBLOB")
    private byte[] photo; // photo du médecin

    @Lob
    @Column(name = "justificatifs", columnDefinition = "LONGBLOB")
    private byte[] documents; // documents justificatifs
}
