package com.example.rml.back_office_rml.dto;

import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class ConsultationDurationDTO {

    private Long id;

    @NotNull(message = "Minutes are required")
    @Min(value = 5, message = "Minutes must be at least 5")
    private Integer minutes;

    @NotBlank(message = "Display name is required")
    private String displayName;

    private Boolean active = true;
}