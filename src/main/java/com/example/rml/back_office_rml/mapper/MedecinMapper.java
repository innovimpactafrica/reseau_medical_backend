package com.example.rml.back_office_rml.mapper;

import com.example.rml.back_office_rml.dto.InscriptionMedecinDto;
import com.example.rml.back_office_rml.entities.Medecin;
import com.example.rml.back_office_rml.entities.Utilisateur;
import com.example.rml.back_office_rml.repositories.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class MedecinMapper {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    /**
     * Convertit un DTO d'inscription en entité Medecin
     * Gère la conversion des MultipartFile en byte[] pour le stockage
     */
   public Medecin toEntity (InscriptionMedecinDto inscriptionMedecinDto) throws IOException {

       System.out.println("=== DEBUG FICHIERS ===");
       System.out.println("Photo reçue: " + (inscriptionMedecinDto.getPhoto() != null ?
               inscriptionMedecinDto.getPhoto().getOriginalFilename() + " (" +
                       inscriptionMedecinDto.getPhoto().getSize() + " bytes)" : "NULL"));

       System.out.println("Justificatifs reçus: " + (inscriptionMedecinDto.getJustificatifs() != null ?
               inscriptionMedecinDto.getJustificatifs().getOriginalFilename() + " (" +
                       inscriptionMedecinDto.getJustificatifs().getSize() + " bytes)" : "NULL"));
        Medecin medecin = new Medecin();
        medecin.setNom(inscriptionMedecinDto.getNom());
        medecin.setPrenom(inscriptionMedecinDto.getPrenom());
        medecin.setTelephone(inscriptionMedecinDto.getTelephone());
        medecin.setSpecialite(inscriptionMedecinDto.getSpecialite());

        // Gestion des fichiers avec métadonnées
        try {
            // Photo
            if (inscriptionMedecinDto.getPhoto() != null && !inscriptionMedecinDto.getPhoto().isEmpty()){
            medecin.setPhoto(inscriptionMedecinDto.getPhoto().getBytes());}

            // Justificatifs
            if (inscriptionMedecinDto.getJustificatifs() != null && !inscriptionMedecinDto.getJustificatifs().isEmpty()) {
                medecin.setJustificatifs(inscriptionMedecinDto.getJustificatifs().getBytes());
        }} catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture des fichiers: " + e.getMessage(), e);
        }

        //Recupération de l'utilisateur référent
        //vérifions si utilisateur existe à la base car on passe de dto à entité
        Optional <Utilisateur> utilisateur = utilisateurRepository.findById(inscriptionMedecinDto.getIdUtilisateurReferent());

        if (utilisateur.isPresent()) {
            medecin.setUtilisateur(utilisateur.get());
        }else {
            throw new RuntimeException("Utilisateur introuvable");
        }
        return medecin;
    }

    //créer un MedecinDTO à partir d'une entité Medecin

   public InscriptionMedecinDto toDTO (Medecin medecin){
        InscriptionMedecinDto medecinDto = new InscriptionMedecinDto();
       medecinDto.setIdUtilisateurReferent(medecin.getUtilisateur().getIdUtilisateur());
        medecinDto.setNom(medecin.getNom());
        medecinDto.setPrenom(medecin.getPrenom());
        medecinDto.setTelephone(medecin.getTelephone());
        medecinDto.setSpecialite(medecin.getSpecialite());

        /** hasPhoto et hasJustificatifs sont des booléens indiquant si des fichiers existent ou non
          Cela permet d'informer le frontend sans transmettre les fichiers lourds (byte[]) , // Ce qui rend la liste plus légère et rapide à charger*/
        medecinDto.setHasPhoto(medecin.getPhoto() != null && medecin.getPhoto().length > 0);
        medecinDto.setHasJustificatifs(medecin.getJustificatifs() != null && medecin.getJustificatifs().length > 0);


        return medecinDto;

    }
}
