package com.example.rml.back_office_rml.mapper;

import com.example.rml.back_office_rml.dto.InscriptionCentreDto;
import com.example.rml.back_office_rml.entities.CentreSante;
import com.example.rml.back_office_rml.entities.Utilisateur;
import com.example.rml.back_office_rml.repositories.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CentreSanteMapper {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    //Créer une entité CentreSante à partir de CentreSanteDTO
    public CentreSante toEntity (InscriptionCentreDto inscriptionCentreDto) {

        CentreSante centreSante = new CentreSante();
        centreSante.setId_centre(inscriptionCentreDto.getId_centre());
        centreSante.setNomCentre(inscriptionCentreDto.getNomCentre());
        centreSante.setAdresseCentre(inscriptionCentreDto.getAdresseCentre());
        centreSante.setNomReferent(inscriptionCentreDto.getNomReferent());
        centreSante.setHoraireOuverture(inscriptionCentreDto.getHoraireOuverture());
        centreSante.setTelephoneReferent(inscriptionCentreDto.getTelephoneReferent());

        //vérifions si utilisateur existe à la base
         Optional <Utilisateur> utilisateur =  utilisateurRepository.findById(inscriptionCentreDto.getIdUtilisateur());
         if (utilisateur.isPresent()) {
             centreSante.setUtilisateur(utilisateur.get());
         }else {
             throw new RuntimeException( "Le référent est introuvable");
         }
        return centreSante;
    }

    //Créer CentreSanteDto à partir de CentreSante
    public InscriptionCentreDto toDto (CentreSante centreSante) {
        InscriptionCentreDto inscriptionCentreDto = new InscriptionCentreDto();
        inscriptionCentreDto.setId_centre(centreSante.getId_centre());
        inscriptionCentreDto.setNomCentre(centreSante.getNomCentre());
        inscriptionCentreDto.setNomReferent(centreSante.getNomReferent());
        inscriptionCentreDto.setAdresseCentre(centreSante.getAdresseCentre());
       inscriptionCentreDto.setHoraireOuverture(centreSante.getHoraireOuverture());
       inscriptionCentreDto.setTelephoneReferent(centreSante.getTelephoneReferent());

        // Récupérer l'ID de l'utilisateur référent
        if (centreSante.getUtilisateur() != null) {
            inscriptionCentreDto.setIdUtilisateur(centreSante.getUtilisateur().getIdUtilisateur());
        }

        return inscriptionCentreDto;

    }
}
