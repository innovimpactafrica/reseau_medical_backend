package com.example.rml.back_office_rml.controllers;

import com.example.rml.back_office_rml.dto.InscriptionCentreDto;
import com.example.rml.back_office_rml.services.CentreSanteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/centres-sante")
@CrossOrigin(origins = "*")
public class CentreSanteController {

    @Autowired
    private CentreSanteService centreSanteService;

    // Inscrire un nouveau centre de santé avec @RequestParam
    @Operation(summary = "Inscrire un nouveau centre de santé")
    @PostMapping("/inscription")
    public ResponseEntity<?> inscrireCentre(
            @Parameter(description = "ID de l'utilisateur référent", required = true)
            @RequestParam Long idUtilisateur,

            @Parameter(description = "Nom du centre", required = true)
            @RequestParam @NotBlank @Size(max = 200) String nomCentre,

            @Parameter(description = "Adresse du centre")
            @RequestParam(required = false) @Size(max = 500) String adresseCentre,

            @Parameter(description = "Horaire d'ouverture")
            @RequestParam(required = false) String horaireOuverture,

            @Parameter(description = "Nom du référent", required = true)
            @RequestParam @NotBlank @Size(max = 100) String nomReferent,

            @Parameter(description = "Téléphone du référent")
            @RequestParam(required = false) @Size(max = 15) String telephoneReferent) {

        try {
            // Créez le DTO manuellement
            InscriptionCentreDto dto = new InscriptionCentreDto();
            dto.setIdUtilisateur(idUtilisateur);
            dto.setNomCentre(nomCentre);
            dto.setAdresseCentre(adresseCentre);
            dto.setHoraireOuverture(horaireOuverture);
            dto.setNomReferent(nomReferent);
            dto.setTelephoneReferent(telephoneReferent);

            InscriptionCentreDto nouveauCentre = centreSanteService.inscrireCentre(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nouveauCentre);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


}