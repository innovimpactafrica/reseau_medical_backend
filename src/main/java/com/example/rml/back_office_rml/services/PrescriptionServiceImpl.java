package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.PrescriptionDTO;
import com.example.rml.back_office_rml.dto.PrescriptionItemDTO;
import com.example.rml.back_office_rml.entities.*;
import com.example.rml.back_office_rml.enums.PrescriptionStatus;
import com.example.rml.back_office_rml.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    public PrescriptionServiceImpl(PrescriptionRepository prescriptionRepository,
                                   MedicalRecordRepository medicalRecordRepository,
                                   DoctorRepository doctorRepository,
                                   AppointmentRepository appointmentRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    @Transactional
    public PrescriptionDTO createPrescription(PrescriptionDTO dto) {

        // VALIDATION 1: Le dossier médical existe
        MedicalRecord record = medicalRecordRepository.findById(dto.getRecordId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Dossier médical non trouvé avec l'ID: " + dto.getRecordId()));

        // VALIDATION 2: Le médecin existe
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Médecin non trouvé avec l'ID: " + dto.getDoctorId()));

        // VALIDATION 3: Le rendez-vous existe (si fourni)
        Appointment appointment = null;
        if (dto.getAppointmentId() != null) {
            appointment = appointmentRepository.findById(dto.getAppointmentId())
                    .orElse(null);
        }

        // VALIDATION 4: Au moins un médicament est prescrit
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new IllegalArgumentException(
                    "L'ordonnance doit contenir au moins un médicament");
        }

        // CRÉATION de l'ordonnance
        Prescription prescription = new Prescription();
        // Génération du numéro unique d'ordonnance
        prescription.setPrescriptionNumber(generatePrescriptionNumber());
        prescription.setMedicalRecord(record);
        prescription.setDoctor(doctor);
        prescription.setAppointment(appointment);
        prescription.setPrescriptionDate(dto.getPrescriptionDate());
        prescription.setValidUntil(dto.getValidUntil());
        prescription.setInstructions(dto.getInstructions());


        /**
         * Transformation de la liste de PrescriptionItemDTO reçue depuis le front-end
         * en une liste d'entités PrescriptionItem liées à la Prescription.
         * Chaque DTO est converti en entité avec ses propriétés, puis lié à la prescription parent.
         * La liste complète est ensuite assignée à la prescription pour que JPA puisse la persister
         * automatiquement grâce au cascade = CascadeType.ALL.
         */
        List<PrescriptionItem> items = dto.getItems().stream()
                .map(itemDto -> {
                    PrescriptionItem item = new PrescriptionItem();
                    item.setMedicationName(itemDto.getMedicationName());
                    item.setDosage(itemDto.getDosage());
                    item.setFrequency(itemDto.getFrequency());
                    item.setDuration(itemDto.getDuration());
                    item.setInstructions(itemDto.getInstructions());
                    item.setPrescription(prescription); // lie l’item à sa prescription
                    return item;
                })
                .collect(Collectors.toList()); // <-- tous les "return item" sont collectés dans une liste


        prescription.setItems(items);

        // SAUVEGARDE
        Prescription saved = prescriptionRepository.save(prescription);
        return convertToDTO(saved);
    }

    @Override
    @Transactional
    public PrescriptionDTO updatePrescription(Long prescriptionId, PrescriptionDTO dto) {

        // Récupération de l'ordonnance existante
        Prescription existing = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Ordonnance non trouvée avec l'ID: " + prescriptionId));

        // VALIDATION: Ne pas modifier une ordonnance expirée ou annulée
        if (existing.getStatus() == PrescriptionStatus.EXPIRED) {
            throw new IllegalStateException("Impossible de modifier une ordonnance expirée");
        }

        if (existing.getStatus() == PrescriptionStatus.CANCELLED) {
            throw new IllegalStateException("Impossible de modifier une ordonnance annulée");
        }

        // MISE À JOUR des champs généraux (si fournis)
        if (dto.getPrescriptionDate() != null) {
            existing.setPrescriptionDate(dto.getPrescriptionDate());
        }

        if (dto.getInstructions() != null) {
            existing.setInstructions(dto.getInstructions());
        }

        if (dto.getStatus() != null) {
            existing.setStatus(dto.getStatus());
        }

        if (dto.getValidUntil() != null) {
            existing.setValidUntil(dto.getValidUntil());
        }

        //  MISE À JOUR des médicaments (si fournis)
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            // Supprimer les anciens items
            existing.getItems().clear();

            List<PrescriptionItem> items = dto.getItems().stream()
                    .map(itemDto -> {
                        PrescriptionItem item = new PrescriptionItem();
                        item.setMedicationName(itemDto.getMedicationName());
                        item.setDosage(itemDto.getDosage());
                        item.setFrequency(itemDto.getFrequency());
                        item.setDuration(itemDto.getDuration());
                        item.setInstructions(itemDto.getInstructions());
                        item.setPrescription(existing); // lie l’item à sa prescription
                        return item;
                    })
                    .collect(Collectors.toList());

            existing.setItems(items);

        }

        // SAUVEGARDE
        Prescription updated = prescriptionRepository.save(existing);
        return convertToDTO(updated);
    }

    @Override
    public List<PrescriptionDTO> getAllPrescriptions() {
        return prescriptionRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PrescriptionDTO> getPrescriptionsByRecordId(Long recordId) {
        // Vérifier que le dossier existe
        if (!medicalRecordRepository.existsById(recordId)) {
            throw new IllegalArgumentException(
                    "Dossier médical non trouvé avec l'ID: " + recordId);
        }

        return prescriptionRepository.findByMedicalRecord_Id(recordId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PrescriptionDTO getPrescriptionByNumber(String prescriptionNumber) {
        Prescription prescription = prescriptionRepository.findByPrescriptionNumber(prescriptionNumber)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Ordonnance non trouvée avec l'ID: " + prescriptionNumber));
        return convertToDTO(prescription);
    }

    // ============================================================================
    // 🔧 MÉTHODES UTILITAIRES
    // ============================================================================

    /**
     * Génère un numéro unique pour l'ordonnance
     * Format: ORD-YYYYMMDD-XXXXX
     */
    private String generatePrescriptionNumber() {
        String date = LocalDate.now().toString().replace("-", "");
        String uuid = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        return "ORD-" + date + "-" + uuid;
    }

    /**
     * Convertit une entité Prescription en DTO
     */
    private PrescriptionDTO convertToDTO(Prescription prescription) {
        PrescriptionDTO dto = new PrescriptionDTO();

        dto.setId(prescription.getId());
        dto.setRecordId(prescription.getMedicalRecord().getId());
        dto.setDoctorId(prescription.getDoctor().getDoctorId());

        if (prescription.getAppointment() != null) {
            dto.setAppointmentId(prescription.getAppointment().getAppointmentId());
        }

        dto.setPrescriptionDate(prescription.getPrescriptionDate());
        dto.setPrescriptionNumber(prescription.getPrescriptionNumber());
        dto.setInstructions(prescription.getInstructions());
        dto.setStatus(prescription.getStatus());
        dto.setValidUntil(prescription.getValidUntil());

        // Informations du médecin
        dto.setDoctorFirstName(prescription.getDoctor().getFirstName());
        dto.setDoctorLastName(prescription.getDoctor().getLastName());
        dto.setDoctorSpecialty(prescription.getDoctor().getSpecialty());

        // Informations du patient
        Patient patient = prescription.getMedicalRecord().getPatient();
        dto.setPatientFirstName(patient.getFirstName());
        dto.setPatientLastName(patient.getLastName());

        // Conversion des items
        List<PrescriptionItemDTO> itemDtos = prescription.getItems().stream()
                .map(this::convertItemToDTO)
                .collect(Collectors.toList());
        dto.setItems(itemDtos);

        dto.setCreatedAt(prescription.getCreatedAt());
        dto.setUpdatedAt(prescription.getUpdatedAt());

        return dto;
    }

    /**
     * Convertit un PrescriptionItem en DTO
     */
    private PrescriptionItemDTO convertItemToDTO(PrescriptionItem item) {
        PrescriptionItemDTO dto = new PrescriptionItemDTO();
        dto.setMedicationName(item.getMedicationName());
        dto.setDosage(item.getDosage());
        dto.setFrequency(item.getFrequency());
        dto.setDuration(item.getDuration());
        dto.setInstructions(item.getInstructions());
        return dto;
    }
}