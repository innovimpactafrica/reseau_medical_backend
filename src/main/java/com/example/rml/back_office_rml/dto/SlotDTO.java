package com.example.rml.back_office_rml.dto;

import com.example.rml.back_office_rml.enums.DayOfWeek;
import com.example.rml.back_office_rml.enums.SlotStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlotDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long slotId;

    // DATE SPÉCIFIQUE (format: dd-MM-yyyy)
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate slotDate;

    // JOUR DE LA SEMAINE (calculé auto ou fourni pour récurrents)
    @JsonIgnore
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Start time is required")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;


    private SlotStatus status;

    // FALSE = créneau ponctuel (une seule date)
    // TRUE = créneau récurrent (chaque semaine)
    private Boolean isRecurring = false;

    @JsonIgnore
    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    @JsonIgnore
    @NotNull(message = "Room ID is required")
    private Long roomId;

    // Informations en lecture seule
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String doctorFirstName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String doctorLastName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String doctorSpecialty;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String roomName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String healthCenterName;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;
}