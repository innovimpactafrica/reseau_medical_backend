package com.example.rml.back_office_rml.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class ConsultationDurationDTO {

    @NotNull(message = "Minutes are required")
    private Integer minutes;

    @NotBlank(message = "Display name is required")
    private String displayName;

    private Boolean active = true;
}