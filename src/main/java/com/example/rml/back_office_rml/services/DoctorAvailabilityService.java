package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.DoctorAvailabilityDTO;

import java.util.List;

public interface DoctorAvailabilityService {

    // Créer une nouvelle disponibilité pour un médecin
    DoctorAvailabilityDTO createAvailability(DoctorAvailabilityDTO doctorAvailabilityDTO);

    // Mettre à jour une disponibilité existante
    DoctorAvailabilityDTO updateAvailability(Long id, DoctorAvailabilityDTO availabilityDTO);

    // Récupérer une disponibilité par son ID
    DoctorAvailabilityDTO getAvailabilityById(Long id);

    // Récupérer toutes les disponibilités d’un médecin
    List<DoctorAvailabilityDTO> getAvailabilitiesByDoctor(Long doctorId);

    // Récupérer toutes les disponibilités d’un centre de santé
    List<DoctorAvailabilityDTO> getAvailabilitiesByHealthCenter(Long healthCenterId);

    // Activer ou désactiver une disponibilité
    DoctorAvailabilityDTO toggleAvailabilityStatus(Long id);

    // Récupérer uniquement les disponibilités actives d’un médecin
    List<DoctorAvailabilityDTO> getActiveAvailabilitiesByDoctor(Long doctorId);

    // Supprimer une disponibilité
    void deleteAvailability(Long id);
}
