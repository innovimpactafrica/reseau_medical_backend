package com.example.rml.back_office_rml.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour une ligne d'ordonnance (un médicament)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionItemDTO {


    // Nom du médicament (obligatoire)
    @NotBlank(message = "Le nom du médicament est obligatoire")
    private String medicationName;

    // Dosage (ex: "500mg", "1 comprimé")
    @NotBlank(message = "Le dosage est obligatoire")
    private String dosage;

    // Fréquence (ex: "2 fois par jour", "Matin et soir")
    @NotBlank(message = "La fréquence est obligatoire")
    private String frequency;

    // Durée du traitement (ex: "10 jours", "1 mois")
    @NotBlank(message = "La durée est obligatoire")
    private String duration;

    // Instructions spécifiques (ex: "À prendre avant les repas")
    private String instructions;
}