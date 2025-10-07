package com.example.rml.back_office_rml.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequestDTO {

    @NotNull(message = "L'identifiant du patient est obligatoire.")
    private Long patientId;

    @NotNull(message = "L'identifiant du créneau est obligatoire.")
    private Long slotId;  // ID du slot disponible choisi


    @Size(max = 255, message = "Le motif de la consultation ne doit pas dépasser 255 caractères.")
    private String consultationReason;  // Motif de la consultation
}
