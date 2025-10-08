package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.HealthCenterDocumentDTO;
import com.example.rml.back_office_rml.entities.HealthCenter;
import com.example.rml.back_office_rml.entities.HealthCenterDocument;
import com.example.rml.back_office_rml.repositories.HealthCenterDocumentRepository;
import com.example.rml.back_office_rml.repositories.HealthCenterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HealthCenterDocumentServiceImpl implements HealthCenterDocumentService {

    private final HealthCenterRepository healthCenterRepository;
    private final HealthCenterDocumentRepository documentRepository;

    @Override
    @Transactional
    public HealthCenterDocumentDTO addHealthCenterDocument(HealthCenterDocumentDTO dto) {
        HealthCenter center = healthCenterRepository.findById(dto.getCenterId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Centre de santé non trouvé avec l'ID: " + dto.getCenterId()));

        HealthCenterDocument document = new HealthCenterDocument();
        document.setHealthCenter(center);
        document.setLogoUrl(dto.getLogoUrl());
        document.setDocumentUrl(dto.getDocumentUrl());

        HealthCenterDocument saved = documentRepository.save(document);
        return convertToDTO(saved);
    }

    @Override
    public List<HealthCenterDocumentDTO> getDocumentsByHealthCenter(Long centerId) {
        return documentRepository.findByHealthCenter_CenterId(centerId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Méthode séparée pour convertir une entité en DTO
    private HealthCenterDocumentDTO convertToDTO(HealthCenterDocument entity) {
        HealthCenterDocumentDTO dto = new HealthCenterDocumentDTO();
        dto.setCenterId(entity.getHealthCenter().getCenterId());
        dto.setLogoUrl(entity.getLogoUrl());
        dto.setDocumentUrl(entity.getDocumentUrl());
        return dto;
    }
}
