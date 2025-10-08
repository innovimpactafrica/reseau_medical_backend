package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.MedicalRecordDTO;
import com.example.rml.back_office_rml.entities.MedicalRecord;
import com.example.rml.back_office_rml.entities.Patient;
import com.example.rml.back_office_rml.repositories.MedicalRecordRepository;
import com.example.rml.back_office_rml.repositories.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;

    public MedicalRecordServiceImpl(MedicalRecordRepository medicalRecordRepository,
                                    PatientRepository patientRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.patientRepository = patientRepository;
    }

    @Override
    @Transactional
    public MedicalRecordDTO createMedicalRecord(MedicalRecordDTO dto) {

        // Vérification 1: Le patient existe
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Patient non trouvé avec l'ID: " + dto.getPatientId()));

        // Vérification 2: Le patient n'a pas déjà un dossier médical
        if (medicalRecordRepository.existsByPatient_PatientId(dto.getPatientId())) {
            throw new IllegalArgumentException(
                    "Ce patient a déjà un dossier médical");
        }

        // Création du dossier médical
        MedicalRecord record = new MedicalRecord();
        record.setPatient(patient);

        // Génération automatique du numéro de dossier si non fourni
        if (dto.getRecordNumber() == null || dto.getRecordNumber().isEmpty()) {
            record.setRecordNumber("DMR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        } else {
            record.setRecordNumber(dto.getRecordNumber());
        }

        record.setBloodType(dto.getBloodType());
        record.setAllergies(dto.getAllergies());
        record.setChronicDiseases(dto.getChronicDiseases());
        record.setCurrentMedications(dto.getCurrentMedications());

        MedicalRecord saved = medicalRecordRepository.save(record);
        return convertToDTO(saved);
    }

    @Override
    @Transactional
    public MedicalRecordDTO updateMedicalRecord(Long recordId, MedicalRecordDTO dto) {

        // Récupération du dossier existant
        MedicalRecord existing = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Dossier médical non trouvé avec l'ID: " + recordId));

        // Mise à jour des champs (si fournis)
        if (dto.getBloodType() != null) {
            existing.setBloodType(dto.getBloodType());
        }
        if (dto.getAllergies() != null) {
            existing.setAllergies(dto.getAllergies());
        }
        if (dto.getChronicDiseases() != null) {
            existing.setChronicDiseases(dto.getChronicDiseases());
        }
        if (dto.getCurrentMedications() != null) {
            existing.setCurrentMedications(dto.getCurrentMedications());
        }

        MedicalRecord updated = medicalRecordRepository.save(existing);
        return convertToDTO(updated);
    }

    @Override
    public List<MedicalRecordDTO> getAllMedicalRecords() {
        return medicalRecordRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MedicalRecordDTO getMedicalRecordByPatientId(Long patientId) {
        MedicalRecord record = medicalRecordRepository.findByPatient_PatientId(patientId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aucun dossier médical trouvé pour le patient ID: " + patientId));
        return convertToDTO(record);
    }



    @Override
    public  MedicalRecordDTO getMedicalRecordByRecordNumber(String recordNumber) {
        MedicalRecord record = medicalRecordRepository.findByRecordNumber(recordNumber)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Dossier médical non trouvé avec le numéro: " + recordNumber));
        return convertToDTO(record);
    }

    // Conversion entité → DTO
    private MedicalRecordDTO convertToDTO(MedicalRecord record) {
        MedicalRecordDTO dto = new MedicalRecordDTO();
        dto.setId(record.getId());
        dto.setPatientId(record.getPatient().getPatientId());
        dto.setRecordNumber(record.getRecordNumber());
        dto.setBloodType(record.getBloodType());
        dto.setAllergies(record.getAllergies());
        dto.setChronicDiseases(record.getChronicDiseases());
        dto.setCurrentMedications(record.getCurrentMedications());
        dto.setPatientFirstName(record.getPatient().getFirstName());
        dto.setPatientLastName(record.getPatient().getLastName());
        dto.setCreatedAt(record.getCreatedAt());
        dto.setUpdatedAt(record.getUpdatedAt());
        return dto;
    }
}