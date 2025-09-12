package com.example.rml.back_office_rml.dto;

import com.example.rml.back_office_rml.enums.RoleUtilisateur;
import com.example.rml.back_office_rml.enums.StatutUtilisateur;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UtilisateurDto {

    @Schema(hidden = true)
    private Long id;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    private String motDePasse;

    @NotNull(message = "Sélectionner votre profil")
    @Schema(description = "Rôle de l'utilisateur", enumAsRef = true)
    private RoleUtilisateur role;

    // Champs masqués dans Swagger
    @Schema(hidden = true)
    private StatutUtilisateur statut;


    @Schema(hidden = true)
    private LocalDateTime dateCreation;
}