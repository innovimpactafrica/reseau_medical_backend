package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.InscriptionMedecinDto;
import com.example.rml.back_office_rml.entities.Medecin;
import com.example.rml.back_office_rml.mapper.MedecinMapper;
import com.example.rml.back_office_rml.repositories.MedecinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;


@Service
public class MedecinServiceImpl implements MedecinService {

    @Autowired
    private MedecinRepository medecinRepository;

    @Autowired
    private MedecinMapper medecinMapper;

    @Override
    public InscriptionMedecinDto inscrireMedecin(InscriptionMedecinDto inscriptionMedecinDto) throws IOException {
        // Vérifier si l'ID utilisateur référent n'est pas null
        if (inscriptionMedecinDto.getIdUtilisateurReferent() == null) {
            throw new RuntimeException("L'ID de l'utilisateur référent ne doit pas être null");
        }

        // Vérifier si un médecin n'existe pas déjà pour cet utilisateur
        Optional<Medecin> medecinExistant = medecinRepository.findByUtilisateurIdUtilisateur(
                inscriptionMedecinDto.getIdUtilisateurReferent());

        if (medecinExistant.isPresent()) {
            throw new RuntimeException("Un médecin existe déjà pour cet utilisateur");
        }
        //Convertir le DTO en entité
         Medecin medecin = medecinMapper.toEntity(inscriptionMedecinDto);

        // Sauvegarder le médecin
        Medecin medecinSauve = medecinRepository.save(medecin);

        // Retourner le DTO
        return medecinMapper.toDTO(medecinSauve);

    }


}