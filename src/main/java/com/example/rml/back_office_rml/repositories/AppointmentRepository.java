package com.example.rml.back_office_rml.repositories;

import com.example.rml.back_office_rml.entities.Appointment;
import com.example.rml.back_office_rml.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

        // Vérifie si un patient a déjà un rendez-vous pour un slot donné
        boolean existsByPatient_PatientIdAndSlot_SlotId(Long patientId, Long slotId);

        // Récupère tous les rendez-vous d'un patient selon un statut donné
        List<Appointment> findByPatient_PatientIdAndStatus(Long patientId, AppointmentStatus status);

        // Récupère tous les rendez-vous d'un médecin selon un statut donné
        List<Appointment> findBySlot_Doctor_DoctorIdAndStatus(Long doctorId, AppointmentStatus status);

        // Récupère un rendez-vous par slot
        Optional<Appointment> findBySlot_SlotId(Long slotId);

        // Récupère un rendez-vous par slot et patient
        Optional<Appointment> findBySlot_SlotIdAndPatient_PatientId(Long slotId, Long patientId);


}
