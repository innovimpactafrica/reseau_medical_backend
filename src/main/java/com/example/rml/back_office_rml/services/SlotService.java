package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.SlotDTO;
import com.example.rml.back_office_rml.enums.MedicalSpecialty;
import com.example.rml.back_office_rml.enums.SlotStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface SlotService {

    //Création d'un créneau
    SlotDTO createSlot(SlotDTO dto);

    //  MISE À JOUR D'UN CRÉNEAU
    SlotDTO updateSlot(Long slotId, SlotDTO dto);

    // RÉCUPÉRATION DES CRÉNEAUX
    SlotDTO getSlotById(Long slotId);

    //par médecin
    List<SlotDTO> getSlotsByDoctor(Long doctorId);

    //Par Salle
    List<SlotDTO> getSlotsByRoom(Long roomId);

    //Par Status
    List<SlotDTO> getSlotsByStatus(SlotStatus status);

    // Tous les créneaux
    List<SlotDTO> getAllSlots();

    //Par centre
    List<SlotDTO> getSlotsByHealthCenter(Long healthCenterId);

    //Par spécialité
    List<SlotDTO> getAvailableSlotsBySpecialty(MedicalSpecialty specialty);


    // Mise à jour uniquement du statut d'un slot
    SlotDTO updateSlotStatus(Long slotId, SlotStatus status);


    //Supprimer un créneau
    void deleteSlot(Long slotId);
}
