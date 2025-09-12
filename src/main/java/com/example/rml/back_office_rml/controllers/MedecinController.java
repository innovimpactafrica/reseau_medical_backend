package com.example.rml.back_office_rml.controllers;


import com.example.rml.back_office_rml.dto.InscriptionMedecinDto;
import com.example.rml.back_office_rml.enums.SpecialiteMedicale;
import com.example.rml.back_office_rml.services.MedecinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/medecin")
public class MedecinController {

     @Autowired
    private  MedecinService medecinService;


    @Operation(summary = "Inscrire un nouveau médecin")
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> inscrireMedecin(
            @Parameter(description = "ID utilisateur référent", required = true)
            @RequestParam Long idUtilisateurReferent,

            @Parameter(description = "Nom du médecin", required = true)
            @RequestParam String nom,

            @Parameter(description = "Prénom du médecin", required = true)
            @RequestParam String prenom,

            @Parameter(description = "Spécialité médicale", required = true)
            @RequestParam SpecialiteMedicale specialite,

            @Parameter(description = "Téléphone", required = true)
            @RequestParam String telephone,

            @Parameter(description = "Photo du médecin")
            @RequestPart(value = "photo", required = false) MultipartFile photo,

            @Parameter(description = "Justificatifs du médecin")
            @RequestPart(value = "justificatifs", required = false) MultipartFile justificatifs) {

        try {
            // Créez le DTO manuellement
            InscriptionMedecinDto dto = new InscriptionMedecinDto();
            dto.setIdUtilisateurReferent(idUtilisateurReferent);
            dto.setNom(nom);
            dto.setPrenom(prenom);
            dto.setSpecialite(specialite);
            dto.setTelephone(telephone);
            dto.setPhoto(photo);
            dto.setJustificatifs(justificatifs);

            // Appelez le service
            InscriptionMedecinDto result = medecinService.inscrireMedecin(dto);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
