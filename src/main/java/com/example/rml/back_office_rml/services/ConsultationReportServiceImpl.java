package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.ConsultationReportDTO;
import com.example.rml.back_office_rml.entities.*;
import com.example.rml.back_office_rml.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsultationReportServiceImpl implements ConsultationReportService {

    private final ConsultationReportRepository reportRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    public ConsultationReportServiceImpl(ConsultationReportRepository reportRepository,
                                         MedicalRecordRepository medicalRecordRepository,
                                         DoctorRepository doctorRepository,
                                         AppointmentRepository appointmentRepository) {
        this.reportRepository = reportRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    @Transactional
    public ConsultationReportDTO createReport(ConsultationReportDTO dto) {

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


        // CRÉATION du compte rendu
        ConsultationReport report = new ConsultationReport();
        report.setMedicalRecord(record);
        report.setDoctor(doctor);
        report.setAppointment(appointment);
        report.setReportDate(dto.getReportDate());
        report.setType(dto.getType());
        report.setTitle(dto.getTitle());
        report.setCategory(dto.getCategory());
        report.setContent(dto.getContent());
        report.setFilePath(dto.getFilePath());
        report.setIsModifiable(dto.getIsModifiable() != null ? dto.getIsModifiable() : true);

        ConsultationReport saved = reportRepository.save(report);
        return convertToDTO(saved);
    }

    @Override
    @Transactional
    public ConsultationReportDTO updateReport(Long reportId, ConsultationReportDTO dto) {

        ConsultationReport existing = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Compte rendu non trouvé avec l'ID: " + reportId));

        // VALIDATION: Ne modifier que les comptes rendus modifiables
        if (Boolean.FALSE.equals(existing.getIsModifiable())) {
            throw new IllegalStateException(
                    "Ce compte rendu ne peut pas être modifié (Audio/Vidéo)");
        }

        //  MISE À JOUR
        if (dto.getReportDate() != null) {
            existing.setReportDate(dto.getReportDate());
        }
        if (dto.getTitle() != null && !dto.getTitle().isEmpty()) {
            existing.setTitle(dto.getTitle());
        }
        if (dto.getCategory() != null) {
            existing.setCategory(dto.getCategory());
        }
        if (dto.getContent() != null) {
            existing.setContent(dto.getContent());
        }

        ConsultationReport updated = reportRepository.save(existing);
        return convertToDTO(updated);
    }

    @Override
    public List<ConsultationReportDTO> getAllReports() {
        return reportRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConsultationReportDTO> getReportsByRecordId(Long recordId) {
        if (!medicalRecordRepository.existsById(recordId)) {
            throw new IllegalArgumentException(
                    "Dossier médical non trouvé avec l'ID: " + recordId);
        }

        return reportRepository.findByMedicalRecord_Id(recordId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }



    // Conversion entité → DTO
    private ConsultationReportDTO convertToDTO(ConsultationReport report) {
        ConsultationReportDTO dto = new ConsultationReportDTO();
        dto.setRecordId(report.getMedicalRecord().getId());
        dto.setDoctorId(report.getDoctor().getDoctorId());

        if (report.getAppointment() != null) {
            dto.setAppointmentId(report.getAppointment().getAppointmentId());
        }

        dto.setReportDate(report.getReportDate());
        dto.setType(report.getType());
        dto.setTitle(report.getTitle());
        dto.setCategory(report.getCategory());
        dto.setContent(report.getContent());
        dto.setFilePath(report.getFilePath());
        dto.setIsModifiable(report.getIsModifiable());

        // Informations du médecin
        dto.setDoctorFirstName(report.getDoctor().getFirstName());
        dto.setDoctorLastName(report.getDoctor().getLastName());
        dto.setDoctorSpecialty(report.getDoctor().getSpecialty());

        // Informations du patient
        Patient patient = report.getMedicalRecord().getPatient();
        dto.setPatientFirstName(patient.getFirstName());
        dto.setPatientLastName(patient.getLastName());

        dto.setCreatedAt(report.getCreatedAt());
        dto.setUpdatedAt(report.getUpdatedAt());

        return dto;
    }
}