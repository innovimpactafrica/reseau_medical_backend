package com.example.rml.back_office_rml.dto;

import com.example.rml.back_office_rml.enums.RoomStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomDTO {

    @NotBlank(message = "Room name is required")
    private String name; // nom ou numéro de la salle

    @Positive(message = "Capacity must be positive")
    private Integer capacity; // capacité de la salle

    @NotNull(message = "Room status is required")
    private RoomStatus status; // statut de la salle

    @JsonIgnore
    @NotNull(message = "Health center ID is required")
    private Long healthCenterId; // ID du centre propriétaire

    // Champs du centre en lecture seule
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String healthCenterName;   // Nom du centre de santé

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String healthCenterAdress; // Adresse du centre de santé

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String healthCenterOpeningHours; // Horaire d'ouverture

    private LocalDateTime createdAt; // date de création
    private LocalDateTime updatedAt; // date de mise à jour
}
