package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.PatientDTO;
import com.example.rml.back_office_rml.entities.Patient;
import com.example.rml.back_office_rml.repositories.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    @Transactional
    public PatientDTO registerPatient(PatientDTO dto) {

        // Validation 1: Prénom obligatoire (Cela vérifie si le champ est vide ou ne contient que des espaces.)
        if (dto.getFirstName() == null || dto.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("Le prénom est obligatoire");
        }

        // Validation 2: Nom obligatoire
        if (dto.getLastName() == null || dto.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom est obligatoire");
        }

        // Validation 3: Téléphone obligatoire et unique
        if (dto.getPhoneNumber() == null || dto.getPhoneNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Le numéro de téléphone est obligatoire");
        }

        if (patientRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new IllegalArgumentException("Ce numéro de téléphone est déjà enregistré");
        }

        // Création du patient
        Patient patient = new Patient();
        patient.setFirstName(dto.getFirstName().trim());
        patient.setLastName(dto.getLastName().trim());
        patient.setPhoneNumber(dto.getPhoneNumber().trim());
        patient.setEmail(dto.getEmail() != null ? dto.getEmail().trim() : null);
        patient.setAddress(dto.getAddress() != null ? dto.getAddress().trim() : null);

        Patient savedPatient = patientRepository.save(patient);
        return convertToDTO(savedPatient);
    }

    @Override
    public PatientDTO getPatientByPhoneNumber(String phoneNumber) {
        Patient patient = patientRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Patient non trouvé avec le numéro: " + phoneNumber));
        return convertToDTO(patient);
    }

    @Override
    public List<PatientDTO> getAllPatients() {
        return patientRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private PatientDTO convertToDTO(Patient patient) {
        PatientDTO dto = new PatientDTO();
        dto.setPatientId(patient.getPatientId());
        dto.setFirstName(patient.getFirstName());
        dto.setLastName(patient.getLastName());
        dto.setPhoneNumber(patient.getPhoneNumber());
        dto.setEmail(patient.getEmail());
        dto.setAddress(patient.getAddress());
        dto.setCreatedAt(patient.getCreatedAt());
        dto.setUpdatedAt(patient.getUpdatedAt());
        return dto;
    }
}