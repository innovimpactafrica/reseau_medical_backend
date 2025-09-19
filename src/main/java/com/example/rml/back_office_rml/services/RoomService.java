package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.RoomDTO;
import com.example.rml.back_office_rml.enums.RoomStatus;

import java.util.List;

public interface RoomService {

    // Créer une nouvelle salle
    RoomDTO  createRoom (RoomDTO roomDTO);

    // Modifier une salle existante
    RoomDTO updateRoom(Long roomId, RoomDTO roomDTO);

    // Lister toutes les salles
    List<RoomDTO> getAllRooms();

    // Lister toutes les salles d'un centre de santé
    List<RoomDTO> getAllRooms(Long healthCenterId);

    // Changer le statut d'une salle
    RoomDTO updateRoomStatus(Long roomId, RoomStatus status);


}
