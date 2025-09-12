package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.UtilisateurDto;
import com.example.rml.back_office_rml.entities.Utilisateur;
import com.example.rml.back_office_rml.enums.StatutUtilisateur;
import com.example.rml.back_office_rml.mapper.UtilisateurMapper;
import com.example.rml.back_office_rml.repositories.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UtilisateurServiceImpl implements  UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private UtilisateurMapper utilisateurMapper;



    @Override
    public UtilisateurDto creerUtilisateur(UtilisateurDto utilisateurDto) {

        // Vérifier si l'email existe déjà
        if (emailExiste(utilisateurDto.getEmail())) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }


        // Convertir DTO en entité
        Utilisateur utilisateur = utilisateurMapper.toEntity(utilisateurDto);


        // Définir le statut par défaut
        utilisateur.setStatut(StatutUtilisateur.EN_ATTENTE);

        // Sauvegarder l'utilisateur
        Utilisateur utilisateurSauve = utilisateurRepository.save(utilisateur);

        // Retourner le DTO
        return utilisateurMapper.toDto(utilisateurSauve);
    }


    @Override
    public List<UtilisateurDto> listerTousLesUtilisateurs() {
        return utilisateurRepository.findAll()
                .stream()
                .map(utilisateurMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean  emailExiste(String email){
        return utilisateurRepository.findByEmail(email).isPresent();
    }
}
