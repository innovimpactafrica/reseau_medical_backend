package com.example.rml.back_office_rml.entities;

import com.example.rml.back_office_rml.enums.UserRole;
import com.example.rml.back_office_rml.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Users { // Classe Utilisateur

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utilisateur") // Nom en base de données
    private Long userId; // idUtilisateur

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "mot_de_passe")
    private String password; // motDePasse

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role; // RoleUtilisateur

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status; // StatutUtilisateur

    @CreationTimestamp
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime creationDate; // dateCreation

    // Relations
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Doctor doctor; // Medecin

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private HealthCenter healthCenter; // CentreSante

    // GETTER pour le frontend (sans le mot de passe)
    public String getPassword() {
        return ""; //
    }



}
