package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.UtilisateurDto;

import java.util.List;

public interface UtilisateurService {

    // Créer un nouvel utilisateur
    UtilisateurDto creerUtilisateur(UtilisateurDto utilisateurDto);

    // Lister tous les utilisateurs
    List<UtilisateurDto> listerTousLesUtilisateurs();

    // Vérifier si un email existe déjà
    boolean emailExiste(String email);

}
