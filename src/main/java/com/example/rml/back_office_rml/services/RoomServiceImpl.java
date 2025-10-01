package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.DefaultTimeSlotDTO;
import com.example.rml.back_office_rml.dto.RoomDTO;
import com.example.rml.back_office_rml.entities.DefaultTimeSlot;
import com.example.rml.back_office_rml.entities.HealthCenter;
import com.example.rml.back_office_rml.entities.Room;
import com.example.rml.back_office_rml.enums.RoomStatus;
import com.example.rml.back_office_rml.enums.UserStatus;
import com.example.rml.back_office_rml.repositories.HealthCenterRepository;
import com.example.rml.back_office_rml.repositories.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final HealthCenterRepository healthCenterRepository;

    public RoomServiceImpl(RoomRepository roomRepository,
                           HealthCenterRepository healthCenterRepository) {
        this.roomRepository = roomRepository;
        this.healthCenterRepository = healthCenterRepository;
    }

    // ====================================================================
    // 📋 CRÉATION D'UNE SALLE
    // ====================================================================
    @Override
    public RoomDTO createRoom(RoomDTO roomDTO) {
        // Vérifier que le centre de santé existe et qu'il est approuvé
        HealthCenter healthCenter = healthCenterRepository.findById(roomDTO.getHealthCenterId())
                .orElseThrow(() -> new IllegalArgumentException("Health center not found"));

        if (healthCenter.getUser().getStatus() != UserStatus.APPROVED){
            throw new RuntimeException("The health center is not approved");
        }

        // Vérifier qu'une salle avec le même nom n'existe pas déjà dans ce centre
        if (roomRepository.existsByNameAndHealthCenter_CenterId(roomDTO.getName(), roomDTO.getHealthCenterId())) {
            throw new IllegalArgumentException("A room with this name already exists in this health center");
        }

        // Créer une nouvelle salle avec les informations de base
        Room room = new Room();
        room.setName(roomDTO.getName());
        room.setCapacity(roomDTO.getCapacity());
        room.setStatus(roomDTO.getStatus() != null ? roomDTO.getStatus() : RoomStatus.AVAILABLE);
        room.setAvailableDays(roomDTO.getAvailableDays());

        // Créer les créneaux horaires si fournis dans le DTO
        if (roomDTO.getDefaultTimeSlotsDto() != null) {
            List<DefaultTimeSlot> defaultTimeSlots = roomDTO.getDefaultTimeSlotsDto().stream()
                    .map(dto -> {
                        DefaultTimeSlot slot = new DefaultTimeSlot();
                        slot.setStartTime(dto.getStartTime());
                        slot.setEndTime(dto.getEndTime());
                        slot.setRoom(room);
                        return slot;
                    })
                    .toList();
            room.setDefaultTimeSlots(defaultTimeSlots);
        }

        room.setHealthCenter(healthCenter);
        Room savedRoom = roomRepository.save(room);
        return convertToDTO(savedRoom);
    }

    // ====================================================================
    // ➕ AJOUT D'UN CRÉNEAU HORAIRE
    // ====================================================================
    @Override
    public RoomDTO addDefaultTimeSlot(Long roomId, DefaultTimeSlotDTO defaultTimeSlotDTO) {
        // Trouver la salle par son ID
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        // Créer un nouveau créneau horaire à partir du DTO
        DefaultTimeSlot newSlot = new DefaultTimeSlot();
        newSlot.setStartTime(defaultTimeSlotDTO.getStartTime());
        newSlot.setEndTime(defaultTimeSlotDTO.getEndTime());
        newSlot.setRoom(room);

        // Ajouter le créneau à la liste des créneaux de la salle
        room.getDefaultTimeSlots().add(newSlot);

        // Sauvegarder la salle
        Room updatedRoom = roomRepository.save(room);
        return convertToDTO(updatedRoom);
    }

    // ====================================================================
    // ✏️ MISE À JOUR D'UNE SALLE
    // ====================================================================
    @Override
    @Transactional
    public RoomDTO updateRoom(Long roomId, RoomDTO roomDTO) {
        // Récupérer la salle existante
        Room existingRoom = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Salle non trouvée avec l'ID: " + roomId));

        //Vérifier si le centre de santé a changé (avec protection null)
        if (roomDTO.getHealthCenterId() != null &&
                !existingRoom.getHealthCenter().getCenterId().equals(roomDTO.getHealthCenterId())) {

            // Cas où l'ID a changé → on récupère le nouveau centre de santé
            HealthCenter newHealthCenter = healthCenterRepository.findById(roomDTO.getHealthCenterId())
                    .orElseThrow(() -> new RuntimeException("Health center not found with ID: " + roomDTO.getHealthCenterId()));

            // On met à jour la salle avec le nouveau centre de santé
            existingRoom.setHealthCenter(newHealthCenter);
        }

         // Vérifier l'unicité du nom (avec protection null)
        if (roomDTO.getName() != null && roomDTO.getHealthCenterId() != null) {
            boolean nameChanged = !existingRoom.getName().equals(roomDTO.getName());
            boolean centerChanged = !existingRoom.getHealthCenter().getCenterId().equals(roomDTO.getHealthCenterId());

            if (nameChanged || centerChanged) {
                if (roomRepository.existsByNameAndHealthCenter_CenterId(roomDTO.getName(), roomDTO.getHealthCenterId())) {
                    throw new RuntimeException("Une salle avec ce nom existe déjà dans ce centre de santé");
                }
            }
        }

        //Mettre à jour le nom seulement si fourni
        if (roomDTO.getName() != null) {
            existingRoom.setName(roomDTO.getName());
        }

        // Mettre à jour les champs de base (on vérifie si roomDTO != null car c'est null pas besoin de changé le champ on garde l'ancienne valeur)
        if (roomDTO.getCapacity() != null){
            existingRoom.setCapacity(roomDTO.getCapacity());
        }

        if (roomDTO.getStatus() != null) {
            existingRoom.setStatus(roomDTO.getStatus());
        }

        // Mettre à jour les jours de disponibilité
        if (roomDTO.getAvailableDays() != null) {
            existingRoom.setAvailableDays(roomDTO.getAvailableDays());
        }

        // Mettre à jour les créneaux horaires
        if (roomDTO.getDefaultTimeSlotsDto() != null) {
            updateTimeSlots(existingRoom, roomDTO.getDefaultTimeSlotsDto());
        }

        Room updatedRoom = roomRepository.save(existingRoom);
        return convertToDTO(updatedRoom);
    }

    // ====================================================================
    // 📊 RÉCUPÉRATION DES SALLES PAR CENTRE DE SANTÉ
    // ====================================================================
    @Override
    public List<RoomDTO> getRoomsByHealthCenter(Long healthCenterId) {
        // Vérifier que le centre existe
        if (!healthCenterRepository.existsById(healthCenterId)) {
            throw new RuntimeException("Centre de santé non trouvé avec l'ID: " + healthCenterId);
        }

        List<Room> rooms = roomRepository.findByHealthCenter_CenterId(healthCenterId);
        return rooms.stream()
                .map(this::convertToDTO)
                .toList();
    }


    // ====================================================================
    // 📋 RÉCUPÉRATION DE TOUTES LES SALLES
    // ====================================================================
    @Override
    public List<RoomDTO> getAllRooms() {
        List<Room> rooms = roomRepository.findAll();
        return rooms.stream()
                .map(this::convertToDTO)
                .toList();
    }

    // ====================================================================
    // 📋 RÉCUPÉRATION DES SALLES PAR  STATUTS
    // ====================================================================

    @Override
    public List<RoomDTO> getRoomsByStatus(RoomStatus status) {
        List <Room> roomList = roomRepository.findByStatus(status);
        return roomList.stream()
                .map(this::convertToDTO).toList();
    }


    // ====================================================================
    // 🗑️ SUPPRESSION D'UNE SALLE
    // ====================================================================
    @Override
    @Transactional
    public void deleteRoom(Long roomId) {
        // Vérifier l'existence de la salle pour éviter les erreurs
        if (!roomRepository.existsById(roomId)) {
            throw new RuntimeException("Salle non trouvée avec l'ID: " + roomId);
        }

        // À COMPLÉTER PLUS TARD : Vérification des affiliations actives
        // Cette sécurité empêchera de supprimer une salle ayant des réservations en cours

        // Suppression propre avec cascade automatique sur les créneaux horaires
        roomRepository.deleteById(roomId);
    }

    // ====================================================================
   // 🔄 MISE À JOUR DU STATUT D'UNE SALLE
   // ====================================================================
    @Transactional
    @Override
    public RoomDTO updateRoomStatus(Long roomId, RoomStatus status) {
        // Récupérer la salle avec gestion d'erreur si non trouvée
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Salle non trouvée avec l'ID: " + roomId));

        try {
            // Appliquer le nouveau statut (ex: AVAILABLE → MAINTENANCE)
            room.setStatus(status);

            // Sauvegarde qui déclenchera la mise à jour automatique de updatedAt
            Room updatedRoom = roomRepository.save(room);

            // Retourner la version DTO pour l'API
            return convertToDTO(updatedRoom);

        } catch (IllegalArgumentException e) {
            // Cas où le statut n'est pas valide (normalement impossible avec l'enum)
            throw new RuntimeException("Statut invalide: " + status);
        }
    }


    // ====================================================================
    // 📋 RÉCUPÉRATION D'UNE SALLE PAR SON ID
    // ====================================================================
    @Override
    public RoomDTO getRoomById(Long roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow(()->
                new RuntimeException("Room with Id: " +roomId+ " not found")
        );

        return convertToDTO(room);


    }


    // ====================================================================
    // 🔧 MÉTHODES PRIVÉES - CONVERSIONS
    // ====================================================================

    /**
     * Convertit une entité Room en DTO RoomDTO
     */
    private RoomDTO convertToDTO(Room room) {
        RoomDTO dto = new RoomDTO();

        // Informations de base de la salle
        dto.setRoomId(room.getRoomId());
        dto.setName(room.getName());
        dto.setCapacity(room.getCapacity());
        dto.setStatus(room.getStatus());
        dto.setAvailableDays(room.getAvailableDays());

        // Informations du centre de santé
        dto.setHealthCenterId(room.getHealthCenter().getCenterId());
        dto.setHealthCenterName(room.getHealthCenter().getName());
        dto.setHealthCenterAddress(room.getHealthCenter().getAddress());
        dto.setHealthCenterOpeningHours(room.getHealthCenter().getOpeningHours());

        // Dates techniques
        dto.setCreatedAt(room.getCreatedAt());
        dto.setUpdatedAt(room.getUpdatedAt());

        // Créneaux horaires
        if (room.getDefaultTimeSlots() != null) {
            List<DefaultTimeSlotDTO> timeSlotsDto = room.getDefaultTimeSlots().stream()
                    .map(this::convertTimeSlotToDTO)
                    .toList();
            dto.setDefaultTimeSlotsDto(timeSlotsDto);
        }

        return dto;
    }

    /**
     * Convertit une entité DefaultTimeSlot en DTO DefaultTimeSlotDTO
     */
    private DefaultTimeSlotDTO convertTimeSlotToDTO(DefaultTimeSlot slot) {
        DefaultTimeSlotDTO dtoSlot = new DefaultTimeSlotDTO();
        dtoSlot.setStartTime(slot.getStartTime());
        dtoSlot.setEndTime(slot.getEndTime());
        return dtoSlot;
    }

    /**
     * Met à jour les créneaux horaires d'une salle
     * @param room La salle dont on veut mettre à jour les créneaux horaires de type Room
     * @param newTimeSlotsDTO La liste des nouveaux créneaux horaires au format DTO de type ist<DefaultTimeSlotDTO>
     */
    private void updateTimeSlots(Room room, List<DefaultTimeSlotDTO> newTimeSlotsDTO) {
        // Supprimer tous les créneaux existants
        room.getDefaultTimeSlots().clear();

        // Ajouter les nouveaux créneaux
        List<DefaultTimeSlot> newTimeSlots = newTimeSlotsDTO.stream()
                .map(dto -> {
                    DefaultTimeSlot slot = new DefaultTimeSlot();
                    slot.setStartTime(dto.getStartTime());
                    slot.setEndTime(dto.getEndTime());
                    slot.setRoom(room);
                    return slot;
                })
                .toList();

        room.getDefaultTimeSlots().addAll(newTimeSlots);
    }

}