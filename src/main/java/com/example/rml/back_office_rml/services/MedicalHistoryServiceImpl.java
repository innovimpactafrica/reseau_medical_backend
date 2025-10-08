package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.MedicalHistoryDTO;
import com.example.rml.back_office_rml.entities.MedicalHistory;
import com.example.rml.back_office_rml.entities.MedicalRecord;
import com.example.rml.back_office_rml.repositories.MedicalHistoryRepository;
import com.example.rml.back_office_rml.repositories.MedicalRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicalHistoryServiceImpl implements MedicalHistoryService {

    private final MedicalHistoryRepository medicalHistoryRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    public MedicalHistoryServiceImpl(MedicalHistoryRepository medicalHistoryRepository,
                                     MedicalRecordRepository medicalRecordRepository) {
        this.medicalHistoryRepository = medicalHistoryRepository;
        this.medicalRecordRepository = medicalRecordRepository;
    }

    @Override
    @Transactional
    public MedicalHistoryDTO addMedicalHistory(MedicalHistoryDTO dto) {

        // Vérification: Le dossier médical existe
        MedicalRecord record = medicalRecordRepository.findById(dto.getRecordId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Dossier médical non trouvé avec l'ID: " + dto.getRecordId()));

        // Création de l'antécédent médical
        MedicalHistory history = new MedicalHistory();
        history.setMedicalRecord(record);
        history.setDiagnosis(dto.getDiagnosis());
        history.setDescription(dto.getDescription());

        MedicalHistory saved = medicalHistoryRepository.save(history);
        return convertToDTO(saved);
    }

    @Override
    @Transactional
    public MedicalHistoryDTO updateMedicalHistory(Long historyId, MedicalHistoryDTO dto) {

        // Récupération de l'antécédent existant
        MedicalHistory existing = medicalHistoryRepository.findById(historyId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Antécédent médical non trouvé avec l'ID: " + historyId));

        // Mise à jour des champs (si fournis)
        if (dto.getDate() != null) {
            existing.setDate(dto.getDate());
        }
        if (dto.getDiagnosis() != null && !dto.getDiagnosis().isEmpty()) {
            existing.setDiagnosis(dto.getDiagnosis());
        }
        if (dto.getDescription() != null) {
            existing.setDescription(dto.getDescription());
        }

        MedicalHistory updated = medicalHistoryRepository.save(existing);
        return convertToDTO(updated);
    }

    @Override
    public List<MedicalHistoryDTO> getAllMedicalHistories() {
        return medicalHistoryRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicalHistoryDTO> getMedicalHistoriesByRecordId(Long recordId) {
        return medicalHistoryRepository.findByMedicalRecord_Id(recordId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Conversion entité → DTO
    private MedicalHistoryDTO convertToDTO(MedicalHistory history) {
        MedicalHistoryDTO dto = new MedicalHistoryDTO();
        dto.setId(history.getId());
        dto.setRecordId(history.getMedicalRecord().getId());
        dto.setDate(history.getDate());
        dto.setDiagnosis(history.getDiagnosis());
        dto.setDescription(history.getDescription());
        dto.setCreatedAt(history.getCreatedAt());
        dto.setUpdatedAt(history.getUpdatedAt());
        return dto;
    }
}