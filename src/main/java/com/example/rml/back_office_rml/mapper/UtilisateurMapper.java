package com.example.rml.back_office_rml.mapper;

import com.example.rml.back_office_rml.dto.UtilisateurDto;
import com.example.rml.back_office_rml.entities.Utilisateur;
import org.springframework.stereotype.Component;

@Component
public class UtilisateurMapper {

    //Créer une entité Utilisateur à partir de UtilisateurDTO
   public  Utilisateur toEntity(UtilisateurDto utilisateurDto) {

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setIdUtilisateur(utilisateurDto.getId());
        utilisateur.setEmail(utilisateurDto.getEmail());
        utilisateur.setMotDePasse(utilisateurDto.getMotDePasse());
        utilisateur.setRole(utilisateurDto.getRole());
        utilisateur.setStatut(utilisateurDto.getStatut());
        utilisateur.setDateCreation(utilisateurDto.getDateCreation());
        return utilisateur;
    }

    //Créer  UtilisateurDto à partir d'une entité Utilisateur
   public UtilisateurDto toDto(Utilisateur utilisateur) {

        UtilisateurDto utilisateurDto = new UtilisateurDto();
        utilisateurDto.setId(utilisateur.getIdUtilisateur());
        utilisateurDto.setEmail(utilisateur.getEmail());
        utilisateurDto.setMotDePasse(utilisateur.getMotDePasse());
        utilisateurDto.setRole(utilisateur.getRole());
        utilisateurDto.setStatut(utilisateur.getStatut());
        utilisateurDto.setDateCreation(utilisateur.getDateCreation());
        return utilisateurDto;
    }


}
