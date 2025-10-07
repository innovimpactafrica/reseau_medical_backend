package com.example.rml.back_office_rml.dto;

import com.example.rml.back_office_rml.enums.SlotStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponseDTO {

    // Informations du slot
    private LocalDate slotDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private SlotStatus status;

    // Informations du patient
    private String patientFirstName;
    private String patientLastName;
    private String patientPhone;

    // Informations du médecin
    private String doctorFirstName;
    private String doctorLastName;
    private String doctorSpecialty;

    // Informations du lieu
    private String roomName;
    private String healthCenterName;

    // Motif de consultation
    private String consultationReason;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;

    private Long slotId;
    private Long patientId;
    private Long doctorId;
}