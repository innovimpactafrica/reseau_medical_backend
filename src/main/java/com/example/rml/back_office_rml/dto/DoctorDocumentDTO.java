package com.example.rml.back_office_rml.dto;

import lombok.Data;

@Data
public class DoctorDocumentDTO {
    private Long id;
    private Long doctorId;      // ID du médecin
    private String documentUrl; // Lien du justificatif
    private String photo;       // Photo du médecin
}
