package com.example.rml.back_office_rml.dto;

import com.example.rml.back_office_rml.enums.DayOfWeek;
import com.example.rml.back_office_rml.enums.RoomStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long roomId;

    @NotBlank(message = "Room name is required")
    private String name;

    @Positive(message = "Capacity must be positive")
    private Integer capacity;

    @NotNull(message = "Room status is required")
    private RoomStatus status;


    @NotNull(message = "Available days are required")
    private Set<DayOfWeek> availableDays;

    @NotNull(message = "Default time slots are required")
    private List<DefaultTimeSlotDTO> defaultTimeSlotsDto;

    @JsonIgnore
    @NotNull(message = "Health center ID is required")
    private Long healthCenterId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String healthCenterName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String healthCenterAddress;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String healthCenterOpeningHours;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;
}
