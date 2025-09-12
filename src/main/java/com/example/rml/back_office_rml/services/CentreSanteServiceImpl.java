package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.InscriptionCentreDto;
import com.example.rml.back_office_rml.entities.CentreSante;
import com.example.rml.back_office_rml.mapper.CentreSanteMapper;
import com.example.rml.back_office_rml.repositories.CentreSanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CentreSanteServiceImpl implements CentreSanteService {
    @Autowired
    private CentreSanteRepository centreSanteRepository;

    @Autowired
    private CentreSanteMapper centreSanteMapper;

    @Override
    public InscriptionCentreDto inscrireCentre(InscriptionCentreDto inscriptionCentreDto) {

        //Verifions si Id referent n'est pas null
        if (inscriptionCentreDto.getIdUtilisateur() == null) {
            throw new RuntimeException("L'id de l'utilisateur ne doit pas être null");
        }

        //Convertissons le centre DTO en entité
        CentreSante centreSante = centreSanteMapper.toEntity(inscriptionCentreDto);

        //Vérifions si  le centre n'existe pas déjà via Id Utilisateur (car ils sont en relation one-to-one)
        Optional<CentreSante> centre = centreSanteRepository.findByUtilisateurIdUtilisateur(inscriptionCentreDto.getIdUtilisateur());
        if (centre.isPresent()) {
            throw new RuntimeException("Le centre de santé  existe déjà");
        }

       CentreSante centreSanteSaved =  centreSanteRepository.save(centreSante);

        return centreSanteMapper.toDto(centreSanteSaved);

    }
}
