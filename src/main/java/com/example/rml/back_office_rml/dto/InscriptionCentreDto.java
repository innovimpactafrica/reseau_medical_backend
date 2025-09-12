package com.example.rml.back_office_rml.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class InscriptionCentreDto {

    @JsonIgnore
    @Schema(hidden = true)
    private Long id_centre;

    private Long idUtilisateur;

    @NotBlank(message = "Le nom du centre est obligatoire")
    @Size(max = 200, message = "Le nom du centre ne peut dépasser 200 caractères")
    private String nomCentre;

    @Size(max = 500, message = "L'adresse ne peut dépasser 500 caractères")
    private String adresseCentre;

    private String horaireOuverture;

    @NotBlank(message = "Le nom du référent est obligatoire")
    @Size(max = 100, message = "Le nom du référent ne peut dépasser 100 caractères")
    private String nomReferent;

    @Size(max = 15, message = "Le téléphone ne peut dépasser 15 caractères")
    private String telephoneReferent;
}