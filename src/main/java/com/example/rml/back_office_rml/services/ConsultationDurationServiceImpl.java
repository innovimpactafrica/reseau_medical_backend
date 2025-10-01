package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.ConsultationDurationDTO;
import com.example.rml.back_office_rml.entities.ConsultationDuration;
import com.example.rml.back_office_rml.repositories.ConsultationDurationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ConsultationDurationServiceImpl implements ConsultationDurationService {

    private final ConsultationDurationRepository durationRepository;

    public ConsultationDurationServiceImpl(ConsultationDurationRepository durationRepository) {
        this.durationRepository = durationRepository;
    }

    // ====================================================================
    // ➕ CRÉATION
    // ====================================================================
    @Override
    public ConsultationDurationDTO createDuration(ConsultationDurationDTO durationDTO) {
        if (durationRepository.findByMinutes(durationDTO.getMinutes()).isPresent()) {
            throw new IllegalArgumentException("Une durée de " + durationDTO.getMinutes() + " minutes existe déjà");
        }

        ConsultationDuration duration = new ConsultationDuration();
        duration.setMinutes(durationDTO.getMinutes());
        duration.setDisplayName(durationDTO.getDisplayName());
        duration.setActive(true);

        return convertToDTO(durationRepository.save(duration));
    }

    // ====================================================================
    // ✏️ MODIFICATION
    // ====================================================================
    @Override
    public ConsultationDurationDTO updateDuration(Long id, ConsultationDurationDTO durationDTO) {
        ConsultationDuration existing = durationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Durée non trouvée avec l'ID: " + id));

        if (!durationDTO.getMinutes().equals(existing.getMinutes())) {
            Optional<ConsultationDuration> conflict = durationRepository.findByMinutes(durationDTO.getMinutes());
            if (conflict.isPresent() && !conflict.get().getId().equals(id)) {
                throw new IllegalArgumentException("Une durée de " + durationDTO.getMinutes() + " minutes existe déjà");
            }
        }

        existing.setMinutes(durationDTO.getMinutes());
        existing.setDisplayName(durationDTO.getDisplayName());

        return convertToDTO(durationRepository.save(existing));
    }

    // ====================================================================
    // 🗑️ SUPPRESSION
    // ====================================================================
    @Override
    public void deleteDuration(Long id) {
        if (!durationRepository.existsById(id)) {
            throw new IllegalArgumentException("Durée non trouvée avec l'ID: " + id);
        }
        durationRepository.deleteById(id);
    }

    // ====================================================================
    // 🔄 ACTIVATION/DÉSACTIVATION
    // ====================================================================
    @Override
    public ConsultationDurationDTO toggleDurationStatus(Long id) {
        ConsultationDuration duration = durationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Durée non trouvée avec l'ID: " + id));

        duration.setActive(!duration.getActive());
        return convertToDTO(durationRepository.save(duration));
    }

    // ====================================================================
    // 📋 RÉCUPÉRATION DE TOUTES LES DURÉES
    // ====================================================================
    @Override
    @Transactional(readOnly = true)
    public List<ConsultationDurationDTO> getAllDurations() {
        return durationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ====================================================================
    // 📋 RÉCUPÉRATION DES DURÉES ACTIVES
    // ====================================================================
    @Override
    @Transactional(readOnly = true)
    public List<ConsultationDurationDTO> getActiveDurations() {
        return durationRepository.findByActiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ====================================================================
    // 🔍 RECHERCHE PAR ID
    // ====================================================================
    @Override
    @Transactional(readOnly = true)
    public Optional<ConsultationDurationDTO> getDurationById(Long id) {
        return durationRepository.findById(id)
                .map(this::convertToDTO);
    }

    // ====================================================================
    // 🔍 RECHERCHE PAR MINUTES
    // ====================================================================
    @Override
    @Transactional(readOnly = true)
    public Optional<ConsultationDurationDTO> getDurationByMinutes(Integer minutes) {
        return durationRepository.findByMinutes(minutes)
                .map(this::convertToDTO);
    }

    // ====================================================================
    // 🔧 CONVERSION ENTITY → DTO
    // ====================================================================
    private ConsultationDurationDTO convertToDTO(ConsultationDuration duration) {
        ConsultationDurationDTO dto = new ConsultationDurationDTO();
        dto.setMinutes(duration.getMinutes());
        dto.setDisplayName(duration.getDisplayName());
        dto.setActive(duration.getActive());
        return dto;
    }
}
