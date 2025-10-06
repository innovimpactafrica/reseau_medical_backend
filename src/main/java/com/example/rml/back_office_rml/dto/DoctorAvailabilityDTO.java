package com.example.rml.back_office_rml.dto;

import com.example.rml.back_office_rml.enums.DayOfWeek;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorAvailabilityDTO {



    // Champs en lecture seule pour la réponse
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String doctorName;


    // Champs en lecture seule pour la réponse
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String healthCenterName;

    @NotNull(message = "Day of week is required")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Start time is required")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    // Champs en lecture seule pour la réponse
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @NotNull(message = "Consultation duration is required")
    private String consultationDurationMin;

    private Boolean active = true;

    @JsonIgnore
    @NotNull(message = "Consultation duration is required")
    private Long consultationDuration_Id;


    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime updatedAt;

    private Long id;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    @NotNull(message = "Health center ID is required")
    private Long healthCenterId;
}