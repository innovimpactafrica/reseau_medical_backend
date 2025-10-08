package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.FamilyHistoryDTO;
import com.example.rml.back_office_rml.entities.FamilyHistory;
import com.example.rml.back_office_rml.entities.MedicalRecord;
import com.example.rml.back_office_rml.repositories.FamilyHistoryRepository;
import com.example.rml.back_office_rml.repositories.MedicalRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FamilyHistoryServiceImpl implements FamilyHistoryService {

    private final FamilyHistoryRepository familyHistoryRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    public FamilyHistoryServiceImpl(FamilyHistoryRepository familyHistoryRepository,
                                    MedicalRecordRepository medicalRecordRepository) {
        this.familyHistoryRepository = familyHistoryRepository;
        this.medicalRecordRepository = medicalRecordRepository;
    }

    @Override
    @Transactional
    public FamilyHistoryDTO addFamilyHistory(FamilyHistoryDTO dto) {

        // Vérification: Le dossier médical existe
        MedicalRecord record = medicalRecordRepository.findById(dto.getRecordId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Dossier médical non trouvé avec l'ID: " + dto.getRecordId()));

        // Création de l'antécédent familial
        FamilyHistory familyHistory = new FamilyHistory();
        familyHistory.setMedicalRecord(record);
        familyHistory.setRelation(dto.getRelation());
        familyHistory.setAge(dto.getAge());
        familyHistory.setCondition(dto.getCondition());
        familyHistory.setNotes(dto.getNotes());

        FamilyHistory saved = familyHistoryRepository.save(familyHistory);
        return convertToDTO(saved);
    }

    @Override
    @Transactional
    public FamilyHistoryDTO updateFamilyHistory(Long familyHistoryId, FamilyHistoryDTO dto) {

        // Récupération de l'antécédent familial existant
        FamilyHistory existing = familyHistoryRepository.findById(familyHistoryId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Antécédent familial non trouvé avec l'ID: " + familyHistoryId));

        // Mise à jour des champs (si fournis)
        if (dto.getRelation() != null) {
            existing.setRelation(dto.getRelation());
        }
        if (dto.getAge() != null) {
            existing.setAge(dto.getAge());
        }
        if (dto.getCondition() != null && !dto.getCondition().isEmpty()) {
            existing.setCondition(dto.getCondition());
        }
        if (dto.getNotes() != null) {
            existing.setNotes(dto.getNotes());
        }

        FamilyHistory updated = familyHistoryRepository.save(existing);
        return convertToDTO(updated);
    }

    @Override
    public List<FamilyHistoryDTO> getAllFamilyHistories() {
        return familyHistoryRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FamilyHistoryDTO> getFamilyHistoriesByRecordId(Long recordId) {
        return familyHistoryRepository.findByMedicalRecord_Id(recordId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Conversion entité → DTO
    private FamilyHistoryDTO convertToDTO(FamilyHistory familyHistory) {
        FamilyHistoryDTO dto = new FamilyHistoryDTO();
        dto.setId(familyHistory.getId());
        dto.setRecordId(familyHistory.getMedicalRecord().getId());
        dto.setRelation(familyHistory.getRelation());
        dto.setAge(familyHistory.getAge());
        dto.setCondition(familyHistory.getCondition());
        dto.setNotes(familyHistory.getNotes());
        dto.setCreatedAt(familyHistory.getCreatedAt());
        dto.setUpdatedAt(familyHistory.getUpdatedAt());
        return dto;
    }
}