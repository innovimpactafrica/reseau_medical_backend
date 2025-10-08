package com.example.rml.back_office_rml.services;
import com.example.rml.back_office_rml.dto.HealthCenterDocumentDTO;

import java.util.List;

public interface HealthCenterDocumentService {

    HealthCenterDocumentDTO addHealthCenterDocument(HealthCenterDocumentDTO dto);
    List<HealthCenterDocumentDTO> getDocumentsByHealthCenter(Long healthCenterId);
}
