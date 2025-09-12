package com.example.rml.back_office_rml.controllers;

import com.example.rml.back_office_rml.dto.UtilisateurDto;
import com.example.rml.back_office_rml.enums.RoleUtilisateur;
import com.example.rml.back_office_rml.services.UtilisateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;

    // Créer un nouvel utilisateur avec @RequestParam
    @Operation(summary = "Créer un nouvel utilisateur")
    @PostMapping
    public ResponseEntity<?> creerUtilisateur(
            @Parameter(description = "Email de l'utilisateur", required = true)
            @RequestParam @Email String email,

            @Parameter(description = "Mot de passe de l'utilisateur", required = true)
            @RequestParam @Size(min = 8) String motDePasse,

            @Parameter(description = "Rôle de l'utilisateur", required = true)
            @RequestParam RoleUtilisateur role) {

        try {
            // Créez le DTO manuellement
            UtilisateurDto dto = new UtilisateurDto();
            dto.setEmail(email);
            dto.setMotDePasse(motDePasse);
            dto.setRole(role);

            UtilisateurDto nouvelUtilisateur = utilisateurService.creerUtilisateur(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nouvelUtilisateur);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Obtenir tous les utilisateurs
    @GetMapping
    @Operation(summary = "Lister les utilisateur")
    public ResponseEntity<List<UtilisateurDto>> obtenirTousLesUtilisateurs() {
        List<UtilisateurDto> utilisateurs = utilisateurService.listerTousLesUtilisateurs();
        return ResponseEntity.ok(utilisateurs);
    }
}