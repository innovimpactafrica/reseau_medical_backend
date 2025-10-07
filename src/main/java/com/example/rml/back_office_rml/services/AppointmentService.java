package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.AppointmentRequestDTO;
import com.example.rml.back_office_rml.dto.AppointmentResponseDTO;
import com.example.rml.back_office_rml.enums.AppointmentStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AppointmentService {

    // Réserver un rendez-vous
    AppointmentResponseDTO bookAppointment(AppointmentRequestDTO request);

    // Obtenir tous les rendez-vous
    List<AppointmentResponseDTO> getAllAppointments();


    // Obtenir tous les rendez-vous d'un patient (filtrer par statut)
    List<AppointmentResponseDTO> getPatientAppointmentsByStatus(Long patientId, AppointmentStatus status);

    // Obtenir tous les rendez-vous d'un médecin (filtrer par statut)
     List<AppointmentResponseDTO> getDoctorAppointmentsByStatus(Long doctorId, AppointmentStatus status);

    // Confirmer un rendez-vous
    AppointmentResponseDTO confirmAppointment(Long appointmentId);

    // Annuler un rendez-vous d'un patient
    void cancelAppointment(Long appointmentId);


    AppointmentResponseDTO terminateAppointment(Long appointmentId);

    // Modifier un rendez-vous (changer le créneau ou le motif)
    AppointmentResponseDTO updateAppointment(Long appointmentId, AppointmentRequestDTO request);

    // Supprimer un rendez-vous
    void deleteAppointment(Long appointmentId);
}
