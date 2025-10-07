package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.AppointmentRequestDTO;
import com.example.rml.back_office_rml.dto.AppointmentResponseDTO;
import com.example.rml.back_office_rml.entities.Appointment;
import com.example.rml.back_office_rml.entities.Patient;
import com.example.rml.back_office_rml.entities.Slot;
import com.example.rml.back_office_rml.enums.AppointmentStatus;
import com.example.rml.back_office_rml.enums.SlotStatus;
import com.example.rml.back_office_rml.repositories.AppointmentRepository;
import com.example.rml.back_office_rml.repositories.PatientRepository;
import com.example.rml.back_office_rml.repositories.SlotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final SlotRepository slotRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;


    public AppointmentServiceImpl(SlotRepository slotRepository,
                                  PatientRepository patientRepository,
                                  AppointmentRepository appointmentRepository) {
        this.slotRepository = slotRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
    }
    @Override
    @Transactional
    public AppointmentResponseDTO bookAppointment(AppointmentRequestDTO request) {

        //Vérification 1: Le patient existe
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Patient non trouvé avec l'ID: " + request.getPatientId()));

        //Vérification 2: Le slot existe
        Slot slot = slotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Créneau non trouvé avec l'ID: " + request.getSlotId()));

        //Vérification 3: Le slot est disponible
        if (slot.getStatus() != SlotStatus.AVAILABLE) {
            throw new IllegalArgumentException("Ce créneau n'est plus disponible");
        }

        /**
         *  Vérification 4 : Gestion des slots récurrents
         *
         * Cette section gère la logique des créneaux (slots) pour les rendez-vous :
         *
         * 1. Si le créneau est **récurrent** (isRecurring = true) :
         *    - On avance la date du slot jusqu'à obtenir une date **future** par rapport à aujourd'hui.
         *    - Exemple : si le slot était prévu le mercredi 01/10/2025 et qu'on est le 06/10/2025,
         *      le créneau sera automatiquement déplacé au mercredi suivant (08/10/2025).
         *    - La récurrence est ici considérée sur une base hebdomadaire.
         *
         * 2. Si le créneau **n'est pas récurrent** :
         *    - On vérifie simplement que la date du slot n'est pas **dans le passé**.
         *    - On prend en compte la date et l'heure pour le même jour :
         *      - Si le slot est aujourd'hui mais que l'heure de fin est déjà passée,
         *        la réservation est refusée.
         *
         * Cette logique permet de :
         *    - Assurer que les patients ne réservent pas de créneaux déjà passés.
         *    - Gérer automatiquement les slots récurrents pour qu'ils restent toujours disponibles
         *      dans le futur sans intervention manuelle.
         */
        if (Boolean.TRUE.equals(slot.getIsRecurring())) {
            // Recupérer la date actuelle
            LocalDate nextDate = slot.getSlotDate();

            //Tant que la date enregistré est passé on va avancer le date du créneau d'une semaine
            while (nextDate.isBefore(LocalDate.now())) {
                nextDate = nextDate.plusWeeks(1); // avancer d'une semaine
            }
            slot.setSlotDate(nextDate);
        } else {
            // Vérification : le slot n'est pas dans le passé pour les slots non récurrents
            if (slot.getSlotDate().isBefore(LocalDate.now()) ||
                    (slot.getSlotDate().isEqual(LocalDate.now()) && slot.getEndTime().isBefore(LocalTime.now()))) {
                throw new IllegalArgumentException("Impossible de réserver un créneau dans le passé");
            }
        }
        // Vérification 5: Le patient n'a pas déjà réservé ce créneau exact
        // (utile seulement pour empêcher double réservation sur le même slot)
        boolean alreadyBooked = appointmentRepository
                .existsByPatient_PatientIdAndSlot_SlotId(patient.getPatientId(), slot.getSlotId());

        if (alreadyBooked) {
            throw new IllegalArgumentException("Vous avez déjà réservé ce créneau.");
        }

        //  Créer le rendez-vous
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setSlot(slot); // lier le slot au rendez-vous
        appointment.setConsultationReason(request.getConsultationReason());
        appointment.setStatus(AppointmentStatus.PENDING);

        //  Mettre à jour le slot : il devient réservé
        slot.setStatus(SlotStatus.RESERVED);
        slotRepository.save(slot); // sauvegarder le slot mis à jour

        //  Sauvegarder le rendez-vous
        appointmentRepository.save(appointment);

        //Retourner le DTO de réponse
        return convertToAppointmentResponse(appointment);
    }


    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getAllAppointments() {
        // Récupère tous les rendez-vous en base
        List<Appointment> appointments = appointmentRepository.findAll();

        // Convertit chaque rendez-vous en DTO pour la réponse
        return appointments.stream()
                .map(this::convertToAppointmentResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getPatientAppointmentsByStatus(Long patientId, AppointmentStatus status) {
        // Récupère tous les rendez-vous pour un patient donné avec un statut spécifique
        List<Appointment> appointments = appointmentRepository
                .findByPatient_PatientIdAndStatus(patientId, status);

        // Convertit les rendez-vous en DTO
        return appointments.stream()
                .map(this::convertToAppointmentResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getDoctorAppointmentsByStatus(Long doctorId, AppointmentStatus status) {
        // Récupère tous les rendez-vous pour un médecin donné avec un statut spécifique
        List<Appointment> appointments = appointmentRepository
                .findBySlot_Doctor_DoctorIdAndStatus(doctorId, status);

        // Convertit les rendez-vous en DTO
        return appointments.stream()
                .map(this::convertToAppointmentResponse)
                .toList();
    }
    @Override
    @Transactional
    public AppointmentResponseDTO confirmAppointment(Long appointmentId) {
        // Récupère le rendez-vous par son ID
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Rendez-vous introuvable."));

        // Vérifie si le slot est expiré
        if (appointment.getSlot().getSlotDate().isBefore(LocalDate.now())) {
            appointment.getSlot().setStatus(SlotStatus.UNAVAILABLE);
            slotRepository.save(appointment.getSlot());
            throw new IllegalStateException("Ce créneau est expiré, impossible de confirmer le rendez-vous.");
        }

        // Vérifie si le rendez-vous est déjà annulé
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException("Impossible de confirmer un rendez-vous déjà annulé.");
        }

        // Si le rendez-vous est terminé
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("Impossible de confirmer un rendez-vous déjà terminé.");
        }

        // Vérifie si le rendez-vous est déjà confirmé
        if (appointment.getStatus() == AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException("Le rendez-vous est déjà confirmé.");
        }

        // Met à jour le statut du rendez-vous et du slot
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.getSlot().setStatus(SlotStatus.RESERVED);

        appointmentRepository.save(appointment);
        slotRepository.save(appointment.getSlot());

        return convertToAppointmentResponse(appointment);
    }


    @Override
    @Transactional
    public void cancelAppointment(Long appointmentId) {
        // Récupère le rendez-vous par son ID
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Rendez-vous introuvable."));

        // Vérifie si le slot est expiré
        if (appointment.getSlot().getSlotDate().isBefore(LocalDate.now())) {
            appointment.getSlot().setStatus(SlotStatus.UNAVAILABLE);
            slotRepository.save(appointment.getSlot());
            throw new IllegalStateException("Ce créneau est déjà expiré, impossible d'annuler le rendez-vous.");
        }

        // Si le rendez-vous est déjà annulé
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException("Ce rendez-vous est déjà annulé.");
        }

        // Si le rendez-vous est terminé
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("Impossible d'annuler un rendez-vous déjà terminé.");
        }

        // Met à jour le statut du rendez-vous et du slot
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.getSlot().setStatus(SlotStatus.AVAILABLE);

        appointmentRepository.save(appointment);
        slotRepository.save(appointment.getSlot());
    }

    @Transactional
    @Override
    public AppointmentResponseDTO terminateAppointment(Long appointmentId) {
        //  Récupère le rendez-vous
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Rendez-vous introuvable avec l'ID : " + appointmentId));

        //  Vérifie si le slot est déjà expiré
        if (appointment.getSlot().getSlotDate().isBefore(LocalDate.now())) {
            appointment.getSlot().setStatus(SlotStatus.UNAVAILABLE);
            slotRepository.save(appointment.getSlot());
            throw new IllegalStateException("Le créneau de ce rendez-vous est déjà expiré, il ne peut plus être terminé.");
        }

        // Vérifie si le rendez-vous est annulé
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException("Impossible de terminer un rendez-vous annulé.");
        }

        //  Vérifie si le rendez-vous n’a jamais été confirmé
        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException("Un rendez-vous doit être confirmé avant d’être marqué comme terminé.");
        }

        //  Met à jour le statut du rendez-vous et du slot
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.getSlot().setStatus(SlotStatus.AVAILABLE); // le slot redevient libre après la consultation

        //  Sauvegarde les changements
        appointmentRepository.save(appointment);
        slotRepository.save(appointment.getSlot());

        return convertToAppointmentResponse(appointment);
    }



    @Override
    @Transactional
    public AppointmentResponseDTO updateAppointment(Long appointmentId, AppointmentRequestDTO request) {
        // Récupère le rendez-vous à mettre à jour
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Rendez-vous introuvable avec l'ID : " + appointmentId));

        // Vérifie si le slot est déjà expiré
        if (appointment.getSlot().getSlotDate().isBefore(LocalDate.now())) {
            appointment.getSlot().setStatus(SlotStatus.UNAVAILABLE);
            slotRepository.save(appointment.getSlot());
            throw new IllegalStateException("Impossible de modifier un rendez-vous dont le créneau est déjà passé.");
        }

        // Vérifie que le rendez-vous peut être modifié
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException("Impossible de modifier un rendez-vous annulé.");
        }

        if (appointment.getStatus() == AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException("Impossible de modifier un rendez-vous déjà confirmé.Il faut l'annuler d'abord");
        }

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("Impossible de modifier un rendez-vous déjà terminé.");
        }

        // Si le slot à modifier est différent du slot actuel
        if (request.getSlotId() != null && !request.getSlotId().equals(appointment.getSlot().getSlotId())) {
            Slot newSlot = slotRepository.findById(request.getSlotId())
                    .orElseThrow(() -> new IllegalArgumentException("Nouveau créneau introuvable."));

            // Vérifie que le nouveau créneau n'est pas passé
            if (newSlot.getSlotDate().isBefore(LocalDate.now())) {
                newSlot.setStatus(SlotStatus.UNAVAILABLE);
                slotRepository.save(newSlot);
                throw new IllegalStateException("Impossible de déplacer le rendez-vous vers un créneau déjà expiré.");
            }

            // Vérifie que le nouveau créneau est disponible
            if (newSlot.getStatus() != SlotStatus.AVAILABLE) {
                throw new IllegalStateException("Le créneau choisi n'est pas disponible.");
            }

            // Libère l'ancien créneau
            appointment.getSlot().setStatus(SlotStatus.AVAILABLE);
            slotRepository.save(appointment.getSlot());

            // Associe le nouveau créneau et le réserve
            appointment.setSlot(newSlot);
            newSlot.setStatus(SlotStatus.RESERVED);
            slotRepository.save(newSlot);
        }

        // Met à jour le motif si fourni
        if (request.getConsultationReason() != null && !request.getConsultationReason().isBlank()) {
            appointment.setConsultationReason(request.getConsultationReason());
        }

        // Sauvegarde les modifications
        Appointment updatedAppointment = appointmentRepository.save(appointment);

        return convertToAppointmentResponse(updatedAppointment);
    }


    @Override
    @Transactional
    public void deleteAppointment(Long appointmentId) {
        // Récupère le rendez-vous
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Rendez-vous introuvable avec l'ID : " + appointmentId));

        // Vérifie si le slot est expiré
        if (appointment.getSlot().getSlotDate().isBefore(LocalDate.now())) {
            appointment.getSlot().setStatus(SlotStatus.UNAVAILABLE);
            slotRepository.save(appointment.getSlot());
            throw new IllegalStateException("Impossible de supprimer un rendez-vous dont le créneau est déjà passé.");
        }

        // Vérifie si le rendez-vous peut être supprimé
        if (appointment.getStatus() == AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException("Impossible de supprimer un rendez-vous confirmé. Annulez-le d'abord.");
        }

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("Impossible de supprimer un rendez-vous terminé.");
        }

        // Libère le créneau avant suppression
        appointment.getSlot().setStatus(SlotStatus.AVAILABLE);
        slotRepository.save(appointment.getSlot());

        // Supprime le rendez-vous
        appointmentRepository.delete(appointment);
    }


    /**
     * Convertit  le rendez-vous en DTO de réponse pour l'API
     */
    private AppointmentResponseDTO convertToAppointmentResponse( Appointment appointment) {
        AppointmentResponseDTO dto = new AppointmentResponseDTO();

        // Informations du slot
        dto.setSlotId(appointment.getSlot().getSlotId());
        dto.setSlotDate(appointment.getSlot().getSlotDate());
        dto.setStartTime(appointment.getSlot().getStartTime());
        dto.setEndTime(appointment.getSlot().getEndTime());
        dto.setStatus(appointment.getSlot().getStatus());

        // Informations du patient
        dto.setPatientId(appointment.getPatient().getPatientId());
        dto.setPatientFirstName(appointment.getPatient().getFirstName());
        dto.setPatientLastName(appointment.getPatient().getLastName());
        dto.setPatientPhone(appointment.getPatient().getPhoneNumber());

        // Informations du médecin
        dto.setDoctorId(appointment.getSlot().getDoctor().getDoctorId());
        dto.setDoctorFirstName(appointment.getSlot().getDoctor().getFirstName());
        dto.setDoctorLastName(appointment.getSlot().getDoctor().getLastName());
        dto.setDoctorSpecialty(appointment.getSlot().getDoctor().getSpecialty().toString());

        // Informations du lieu
        dto.setRoomName(appointment.getSlot().getRoom().getName());
        dto.setHealthCenterName(appointment.getSlot().getRoom().getHealthCenter().getName());

        // Motif de consultation
        dto.setConsultationReason(appointment.getConsultationReason());

        // Dates
        dto.setCreatedAt(appointment.getSlot().getCreatedAt());
        dto.setUpdatedAt(appointment.getSlot().getUpdatedAt());

        return dto;
    }



}