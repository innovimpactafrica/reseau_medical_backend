package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.ConsultationDurationDTO;

import java.util.List;
import java.util.Optional;

public interface ConsultationDurationService {

    // CRUD avec DTO

    // Créer une nouvelle durée de consultation
    ConsultationDurationDTO createDuration(ConsultationDurationDTO durationDTO);

    // Mettre à jour une durée existante
    ConsultationDurationDTO updateDuration(Long id, ConsultationDurationDTO durationDTO);

    // Supprimer une durée via son ID
    void deleteDuration(Long id);

    // Activer ou désactiver une durée
    ConsultationDurationDTO toggleDurationStatus(Long id);


    // Recherches avec DTO

    // Récupérer toutes les durées
    List<ConsultationDurationDTO> getAllDurations();

    // Récupérer uniquement les durées actives
    List<ConsultationDurationDTO> getActiveDurations();

    // Trouver une durée par son ID
    Optional<ConsultationDurationDTO> getDurationById(Long id);

    // Trouver une durée par sa valeur en minutes
    Optional<ConsultationDurationDTO> getDurationByMinutes(Integer minutes);


}
