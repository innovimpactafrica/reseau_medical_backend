package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.DoctorAvailabilityDTO;
import com.example.rml.back_office_rml.entities.ConsultationDuration;
import com.example.rml.back_office_rml.entities.Doctor;
import com.example.rml.back_office_rml.entities.DoctorAvailability;
import com.example.rml.back_office_rml.entities.HealthCenter;
import com.example.rml.back_office_rml.repositories.ConsultationDurationRepository;
import com.example.rml.back_office_rml.repositories.DoctorAvailabilityRepository;
import com.example.rml.back_office_rml.repositories.DoctorRepository;
import com.example.rml.back_office_rml.repositories.HealthCenterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorAvailabilityServiceImpl implements DoctorAvailabilityService {

    private final DoctorRepository doctorRepository;
    private final HealthCenterRepository healthCenterRepository;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;
    private final ConsultationDurationRepository consultationDurationRepository;

    public DoctorAvailabilityServiceImpl(DoctorRepository doctorRepository,
                                         HealthCenterRepository healthCenterRepository,
                                         DoctorAvailabilityRepository doctorAvailabilityRepository,
                                         ConsultationDurationRepository consultationDurationRepository) {
        this.doctorRepository = doctorRepository;
        this.healthCenterRepository = healthCenterRepository;
        this.doctorAvailabilityRepository = doctorAvailabilityRepository;
        this.consultationDurationRepository = consultationDurationRepository;
    }

    // ====================================================================
    // ➕ CRÉATION D'UNE DISPONIBILITÉ
    // ====================================================================
    @Override
    public DoctorAvailabilityDTO createAvailability(DoctorAvailabilityDTO dto) {

        // Validation des heures : début < fin
        if (dto.getStartTime().isAfter(dto.getEndTime()) || dto.getStartTime().equals(dto.getEndTime())) {
            throw new IllegalArgumentException("L'heure de début doit être avant l'heure de fin");
        }

        // Vérifier que le médecin existe
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Médecin non trouvé"));

        // Vérifier que le centre de santé existe
        HealthCenter healthCenter = healthCenterRepository.findById(dto.getHealthCenterId())
                .orElseThrow(() -> new IllegalArgumentException("Centre de santé non trouvé"));

        // Vérifier que la durée de consultation existe
        ConsultationDuration consultationDuration = consultationDurationRepository.findById(dto.getConsultationDuration_Id())
                .orElseThrow(() -> new IllegalArgumentException("Durée de consultation non trouvée"));

        // Vérifier les conflits de disponibilités pour le même médecin et centre
        List<DoctorAvailability> conflicts = doctorAvailabilityRepository.findConflictingAvailabilities(
                dto.getDoctorId(),
                dto.getHealthCenterId(),
                dto.getDayOfWeek(),
                dto.getStartTime(),
                dto.getEndTime()
        );
        if (!conflicts.isEmpty()) {
            throw new IllegalArgumentException("Le médecin a déjà une disponibilité sur ce créneau horaire");
        }

        // Création de l'entité disponibilité
        DoctorAvailability availability = new DoctorAvailability();
        availability.setDoctor(doctor);
        availability.setHealthCenter(healthCenter);
        availability.setDayOfWeek(dto.getDayOfWeek());
        availability.setStartTime(dto.getStartTime());
        availability.setEndTime(dto.getEndTime());
        availability.setConsultationDuration(consultationDuration);
        availability.setIsRecurring(dto.getIsRecurring());
        availability.setActive(true);

        DoctorAvailability savedAvailability = doctorAvailabilityRepository.save(availability);
        return convertToDTO(savedAvailability);
    }

    // ====================================================================
    // ✏️ MISE À JOUR D'UNE DISPONIBILITÉ
    // ====================================================================
    @Override
    public DoctorAvailabilityDTO updateAvailability(Long id, DoctorAvailabilityDTO dto) {
        DoctorAvailability existing = doctorAvailabilityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Disponibilité non trouvée"));

        // Vérification cohérence des heures
        if (dto.getStartTime().isAfter(dto.getEndTime()) || dto.getStartTime().equals(dto.getEndTime())) {
            throw new IllegalArgumentException("L'heure de début doit être avant l'heure de fin");
        }

        // Vérifier si le centre de santé a changé
        HealthCenter newHealthCenter = existing.getHealthCenter();
        if (!existing.getHealthCenter().getCenterId().equals(dto.getHealthCenterId())) {
            newHealthCenter = healthCenterRepository.findById(dto.getHealthCenterId())
                    .orElseThrow(() -> new IllegalArgumentException("Nouveau centre de santé non trouvé"));
        }

        // Vérifier si la durée de consultation a changé
        ConsultationDuration newConsultationDuration = existing.getConsultationDuration();
        if (!existing.getConsultationDuration().getId().equals(dto.getConsultationDuration_Id())) {
            newConsultationDuration = consultationDurationRepository.findById(dto.getConsultationDuration_Id())
                    .orElseThrow(() -> new IllegalArgumentException("Nouvelle durée non trouvée"));
        }

        // Vérifier les conflits de disponibilités (exclure la disponibilité actuelle)
        List<DoctorAvailability> conflicts = doctorAvailabilityRepository.findConflictingAvailabilities(
                existing.getDoctor().getDoctorId(),
                dto.getHealthCenterId(),
                dto.getDayOfWeek(),
                dto.getStartTime(),
                dto.getEndTime()
        );
        if (!conflicts.isEmpty()) {
            throw new IllegalArgumentException("Le médecin a déjà une disponibilité sur ce créneau horaire");
        }

        // Mise à jour des champs
        existing.setHealthCenter(newHealthCenter);
        existing.setDayOfWeek(dto.getDayOfWeek());
        existing.setStartTime(dto.getStartTime());
        existing.setEndTime(dto.getEndTime());
        existing.setConsultationDuration(newConsultationDuration);
        existing.setIsRecurring(dto.getIsRecurring());

        DoctorAvailability updated = doctorAvailabilityRepository.save(existing);
        return convertToDTO(updated);
    }

    // ====================================================================
    // 📋 RÉCUPÉRATION PAR ID
    // ====================================================================
    @Override
    public DoctorAvailabilityDTO getAvailabilityById(Long id) {
        DoctorAvailability availability = doctorAvailabilityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Disponibilité non trouvée"));
        return convertToDTO(availability);
    }

    // ====================================================================
    // 📋 RÉCUPÉRATION PAR MÉDECIN
    // ====================================================================
    @Override
    public List<DoctorAvailabilityDTO> getAvailabilitiesByDoctor(Long doctorId) {
        return doctorAvailabilityRepository.findByDoctorDoctorId(doctorId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ====================================================================
    // 📋 RÉCUPÉRATION PAR CENTRE DE SANTÉ
    // ====================================================================
    @Override
    public List<DoctorAvailabilityDTO> getAvailabilitiesByHealthCenter(Long healthCenterId) {
        return doctorAvailabilityRepository.findByHealthCenterCenterId(healthCenterId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ====================================================================
    // 🔄 ACTIVATION / DÉSACTIVATION
    // ====================================================================
    @Override
    @Transactional
    public DoctorAvailabilityDTO toggleAvailabilityStatus(Long id) {
        DoctorAvailability availability = doctorAvailabilityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Disponibilité non trouvée"));

        availability.setActive(!availability.getActive());
        DoctorAvailability updated = doctorAvailabilityRepository.save(availability);
        return convertToDTO(updated);
    }

    // ====================================================================
    // 📋 RÉCUPÉRATION DES DISPONIBILITÉS ACTIVES D’UN MÉDECIN
    // ====================================================================
    @Override
    public List<DoctorAvailabilityDTO> getActiveAvailabilitiesByDoctor(Long doctorId) {
        List<DoctorAvailability> activeAvailabilities = doctorAvailabilityRepository.findByDoctorDoctorIdAndActiveTrue(doctorId);
        return activeAvailabilities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ====================================================================
    // 🗑️ SUPPRESSION D'UNE DISPONIBILITÉ
    // ====================================================================
    @Override
    @Transactional
    public void deleteAvailability(Long id) {
        if (!doctorAvailabilityRepository.existsById(id)) {
            throw new IllegalArgumentException("Disponibilité non trouvée");
        }
        doctorAvailabilityRepository.deleteById(id);
    }

    // ====================================================================
    // 🔧 CONVERSION ENTITY → DTO
    // ====================================================================
    private DoctorAvailabilityDTO convertToDTO(DoctorAvailability availability) {
        DoctorAvailabilityDTO dto = new DoctorAvailabilityDTO();
        dto.setId(availability.getId());
        dto.setDoctorId(availability.getDoctor().getDoctorId());
        dto.setHealthCenterId(availability.getHealthCenter().getCenterId());
        dto.setDayOfWeek(availability.getDayOfWeek());
        dto.setStartTime(availability.getStartTime());
        dto.setEndTime(availability.getEndTime());
        dto.setConsultationDurationMin(availability.getConsultationDuration().getDisplayName());
        dto.setIsRecurring(availability.getIsRecurring());
        dto.setActive(availability.getActive());
        dto.setDoctorName(availability.getDoctor().getFirstName() + " " + availability.getDoctor().getLastName());
        dto.setHealthCenterName(availability.getHealthCenter().getName());
        dto.setCreatedAt(availability.getCreatedAt());
        dto.setUpdatedAt(availability.getUpdatedAt());
        return dto;
    }
}
