package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.ExaminationDTO;
import com.example.rml.back_office_rml.entities.Doctor;
import com.example.rml.back_office_rml.entities.Examination;
import com.example.rml.back_office_rml.entities.MedicalRecord;
import com.example.rml.back_office_rml.repositories.DoctorRepository;
import com.example.rml.back_office_rml.repositories.ExaminationRepository;
import com.example.rml.back_office_rml.repositories.MedicalRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExaminationServiceImpl implements ExaminationService {

    private final ExaminationRepository examinationRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final DoctorRepository doctorRepository;

    public ExaminationServiceImpl(ExaminationRepository examinationRepository,
                                  MedicalRecordRepository medicalRecordRepository,
                                  DoctorRepository doctorRepository) {
        this.examinationRepository = examinationRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.doctorRepository = doctorRepository;
    }

    @Override
    @Transactional
    public ExaminationDTO addExamination(ExaminationDTO dto) {

        // Vérification 1: Le dossier médical existe
        MedicalRecord record = medicalRecordRepository.findById(dto.getRecordId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Medical record not found with ID: " + dto.getRecordId()));

        // Vérification 2: Le médecin existe
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Doctor not found with ID: " + dto.getDoctorId()));

        // Création de l'examen médical
        Examination examination = new Examination();
        examination.setMedicalRecord(record);
        examination.setDoctor(doctor);
        examination.setExaminationDate(dto.getExaminationDate());
        examination.setType(dto.getType());
        examination.setTitle(dto.getTitle());
        examination.setResults(dto.getResults());
        examination.setNotes(dto.getNotes());

        // Ajout des fichiers (si fournis)
        if (dto.getResultFiles() != null && !dto.getResultFiles().isEmpty()) {
            examination.setResultFiles(dto.getResultFiles()); // on récupère toutes les URLs
        }

        Examination saved = examinationRepository.save(examination);
        return convertToDTO(saved);
    }

    @Override
    @Transactional
    public ExaminationDTO updateExamination(Long examinationId, ExaminationDTO dto) {

        // Récupération de l'examen existant
        Examination existing = examinationRepository.findById(examinationId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Examen médical non trouvé avec l'ID: " + examinationId));

        // Mise à jour des champs (si fournis)
        if (dto.getExaminationDate() != null) {
            existing.setExaminationDate(dto.getExaminationDate());
        }
        if (dto.getType() != null) {
            existing.setType(dto.getType());
        }
        if (dto.getTitle() != null && !dto.getTitle().isEmpty()) {
            existing.setTitle(dto.getTitle());
        }
        if (dto.getResults() != null) {
            existing.setResults(dto.getResults());
        }
        if (dto.getNotes() != null) {
            existing.setNotes(dto.getNotes());
        }
        if (dto.getResultFiles() != null && !dto.getResultFiles().isEmpty()) {
            existing.setResultFiles(dto.getResultFiles());
        }

        Examination updated = examinationRepository.save(existing);
        return convertToDTO(updated);
    }

    @Override
    public List<ExaminationDTO> getAllExaminations() {
        return examinationRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExaminationDTO> getExaminationsByRecordId(Long recordId) {
        return examinationRepository.findByMedicalRecord_Id(recordId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Conversion entité → DTO
    private ExaminationDTO convertToDTO(Examination examination) {
        ExaminationDTO dto = new ExaminationDTO();
        dto.setId(examination.getId());
        dto.setRecordId(examination.getMedicalRecord().getId());
        dto.setDoctorId(examination.getDoctor().getDoctorId());
        dto.setExaminationDate(examination.getExaminationDate());
        dto.setType(examination.getType());
        dto.setTitle(examination.getTitle());
        dto.setResults(examination.getResults());
        dto.setNotes(examination.getNotes());
        dto.setResultFiles(examination.getResultFiles());
        dto.setDoctorFirstName(examination.getDoctor().getFirstName());
        dto.setDoctorLastName(examination.getDoctor().getLastName());
        dto.setDoctorSpecialty(examination.getDoctor().getSpecialty());
        dto.setCreatedAt(examination.getCreatedAt());
        dto.setUpdatedAt(examination.getUpdatedAt());
        return dto;
    }
}