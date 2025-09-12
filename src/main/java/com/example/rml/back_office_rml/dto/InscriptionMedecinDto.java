package com.example.rml.back_office_rml.dto;

import com.example.rml.back_office_rml.enums.SpecialiteMedicale;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class InscriptionMedecinDto {

    private Long idUtilisateurReferent;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne peut dépasser 100 caractères")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100, message = "Le prénom ne peut dépasser 100 caractères")
    private String prenom;

    @NotNull(message = "La spécialité est obligatoire")
    private SpecialiteMedicale specialite;

    @NotBlank(message = "Le téléphone est obligatoire")
    @Size(max = 15, message = "Le téléphone ne peut dépasser 15 caractères")
    private String telephone;

    // En retournant le DTO Medecin on les affiche pas puisqu'on passe pas de Byte à Multipart (alternative : les indicateurs de présence)
    @JsonIgnore
    private MultipartFile photo;
    @JsonIgnore
    private MultipartFile justificatifs;

    // Indicateurs de présence des fichiers
    private boolean hasPhoto = false;
    private boolean hasJustificatifs = false;
}
