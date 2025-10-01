package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.DefaultTimeSlotDTO;
import com.example.rml.back_office_rml.dto.RoomDTO;
import com.example.rml.back_office_rml.enums.RoomStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RoomService {

    /**
     * Crée une nouvelle salle avec les informations fournies
     * @param roomDTO Données de la salle à créer
     * @return La salle créée avec son ID généré
     */
    RoomDTO createRoom(RoomDTO roomDTO);

    /**
     * Retourne la liste complète de toutes les salles
     * @return Liste des salles au format DTO
     */
    List<RoomDTO> getAllRooms();

    /**
     * Ajoute un créneau horaire à une salle existante
     * @param roomId ID de la salle à modifier
     * @param defaultTimeSlotDTO Données du créneau à ajouter
     * @return La salle mise à jour avec le nouveau créneau
     */
    RoomDTO addDefaultTimeSlot(Long roomId, DefaultTimeSlotDTO defaultTimeSlotDTO);

    // Modifier une salle existante
    RoomDTO updateRoom(Long roomId, RoomDTO roomDTO);

    // Lister toutes les salles d'un centre de santé
    List<RoomDTO> getRoomsByHealthCenter(Long healthCenterId);

    // Supprimer une salle
    void deleteRoom(Long roomId);

    // Changer le statut d'une salle
    RoomDTO updateRoomStatus(Long roomId, RoomStatus status);

    //Trouver une salle par son id
    RoomDTO getRoomById(Long roomId);

    //Lister les salles par statut
    List <RoomDTO> getRoomsByStatus(RoomStatus status);
}