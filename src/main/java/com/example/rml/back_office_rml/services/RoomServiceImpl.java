package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.RoomDTO;
import com.example.rml.back_office_rml.entities.HealthCenter;
import com.example.rml.back_office_rml.entities.Room;
import com.example.rml.back_office_rml.enums.RoomStatus;
import com.example.rml.back_office_rml.repositories.HealthCenterRepository;
import com.example.rml.back_office_rml.repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    HealthCenterRepository healthCenterRepository;
    @Autowired
    private RoomRepository roomRepository;

    @Override
    public RoomDTO createRoom(RoomDTO roomDTO) {

        // Vérifier que le centre de santé existe
        HealthCenter healthCenter = healthCenterRepository.findById(roomDTO.getHealthCenterId())
                .orElseThrow(() -> new IllegalArgumentException("Health center not found"));

        // Vérifier qu'une salle avec le même nom n'existe pas déjà dans ce centre
        if (roomRepository.existsByNameAndHealthCenter_CenterId(roomDTO.getName(), roomDTO.getHealthCenterId())){
            throw new IllegalArgumentException("A room with this name already exists in this health center");
        }

        // Créer l'entité Room à partir du DTO
        Room room = new Room();
        room.setName(roomDTO.getName());
        room.setCapacity(roomDTO.getCapacity());
        room.setStatus(roomDTO.getStatus());
        room.setHealthCenter(healthCenter);
        Room savedRoom = roomRepository.save(room);
        return convertToDTO(savedRoom);
    }



    @Override
    public RoomDTO updateRoom(Long roomId, RoomDTO roomDTO) {
        return null;
    }

    @Override
    public List<RoomDTO> getAllRooms() {
        return List.of();
    }

    @Override
    public List<RoomDTO> getAllRooms(Long healthCenterId) {
        return List.of();
    }

    @Override
    public RoomDTO updateRoomStatus(Long roomId, RoomStatus status) {
        return null;
    }

    private RoomDTO convertToDTO(Room room) {
        RoomDTO dto = new RoomDTO();
        dto.setName(room.getName());
        dto.setCapacity(room.getCapacity());
        dto.setStatus(room.getStatus());
        dto.setCreatedAt(room.getCreatedAt());
        dto.setUpdatedAt(room.getUpdatedAt());
        dto.setHealthCenterName(room.getHealthCenter().getName());
        dto.setHealthCenterAdress(room.getHealthCenter().getAddress());
        dto.setHealthCenterOpeningHours(room.getHealthCenter().getOpeningHours());
        return dto;
    }
}
