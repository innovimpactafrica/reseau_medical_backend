    package com.example.rml.back_office_rml.dto;

    import lombok.Data;
    import java.util.List;

    @Data
    public class HealthCenterDocumentDTO {
        private Long centerId;
        private String logoUrl;
        private String documentUrl; // liste reçue depuis le front (avant concaténation)
    }
