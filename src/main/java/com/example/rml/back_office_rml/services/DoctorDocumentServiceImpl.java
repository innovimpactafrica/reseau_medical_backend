package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.DoctorDocumentDTO;
import com.example.rml.back_office_rml.entities.Doctor;
import com.example.rml.back_office_rml.entities.DoctorDocument;
import com.example.rml.back_office_rml.repositories.DoctorDocumentRepository;
import com.example.rml.back_office_rml.repositories.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorDocumentServiceImpl implements DoctorDocumentService {

    private final DoctorRepository doctorRepository;
    private final DoctorDocumentRepository documentRepository;

    @Override
    @Transactional
    public DoctorDocumentDTO addDoctorDocument(DoctorDocumentDTO dto) {

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Médecin non trouvé avec l'ID: " + dto.getDoctorId()));

        DoctorDocument document = new DoctorDocument();
        document.setDoctor(doctor);
        document.setDocumentUrl(dto.getDocumentUrl());
        document.setPhoto(dto.getPhoto());

        DoctorDocument saved = documentRepository.save(document);
        return convertToDTO(saved);
    }

    @Override
    public List<DoctorDocumentDTO> getDocumentsByDoctor(Long doctorId) {
        return documentRepository.findByDoctor_DoctorId(doctorId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private DoctorDocumentDTO convertToDTO(DoctorDocument entity) {
        DoctorDocumentDTO dto = new DoctorDocumentDTO();
        dto.setId(entity.getId());
        dto.setDoctorId(entity.getDoctor().getDoctorId());
        dto.setDocumentUrl(entity.getDocumentUrl());
        dto.setPhoto(entity.getPhoto());
        return dto;
    }
}
