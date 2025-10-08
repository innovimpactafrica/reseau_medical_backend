package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.DoctorDocumentDTO;
import java.util.List;

public interface DoctorDocumentService {

    DoctorDocumentDTO addDoctorDocument(DoctorDocumentDTO dto);
    List<DoctorDocumentDTO> getDocumentsByDoctor(Long doctorId);
}
