package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.SlotDTO;
import com.example.rml.back_office_rml.entities.Doctor;
import com.example.rml.back_office_rml.entities.Room;
import com.example.rml.back_office_rml.entities.Slot;
import com.example.rml.back_office_rml.enums.DayOfWeek;
import com.example.rml.back_office_rml.enums.MedicalSpecialty;
import com.example.rml.back_office_rml.enums.RoomStatus;
import com.example.rml.back_office_rml.enums.SlotStatus;
import com.example.rml.back_office_rml.repositories.DoctorAvailabilityRepository;
import com.example.rml.back_office_rml.repositories.DoctorRepository;
import com.example.rml.back_office_rml.repositories.RoomRepository;
import com.example.rml.back_office_rml.repositories.SlotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SlotServiceImpl implements SlotService {

    private final SlotRepository slotRepository;
    private final DoctorRepository doctorRepository;
    private final RoomRepository roomRepository;
    private final DoctorAvailabilityRepository availabilityRepository;

    public SlotServiceImpl(SlotRepository slotRepository,
                           DoctorRepository doctorRepository,
                           RoomRepository roomRepository,
                           DoctorAvailabilityRepository availabilityRepository) {
        this.slotRepository = slotRepository;
        this.doctorRepository = doctorRepository;
        this.roomRepository = roomRepository;
        this.availabilityRepository = availabilityRepository;
    }


    // ➕ CRÉATION D'UN CRÉNEAU (AVEC DATE SPÉCIFIQUE)
    // ====================================================================
    @Override
    @Transactional
    public SlotDTO createSlot(SlotDTO dto) {

        // VALIDATION 1: Date obligatoire
        if (dto.getSlotDate() == null) {
            throw new IllegalArgumentException("La date du créneau est obligatoire");
        }

        // VALIDATION 2: Date pas dans le passé
        if (dto.getSlotDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Impossible de créer un créneau dans le passé");
        }

        // VALIDATION 3: Heures cohérentes
        validateTimeRange(dto.getStartTime(), dto.getEndTime());

        // VALIDATION 4: Le médecin existe
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Médecin non trouvé avec l'ID: " + dto.getDoctorId()));

        // VALIDATION 5: La salle existe
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Salle non trouvée avec l'ID: " + dto.getRoomId()));

        // Calculer le jour de la semaine depuis la date
        DayOfWeek dayOfWeek = convertToDayOfWeek(dto.getSlotDate().getDayOfWeek());

        // Vérification si la salle est disponible
        if (room.getStatus() != RoomStatus.AVAILABLE) {
            throw new IllegalArgumentException(
                    "La salle est indisponible");
        }

        // VALIDATION 6: La salle est disponible ce jour-là
        if (!room.getAvailableDays().contains(dayOfWeek)) {
            throw new IllegalArgumentException(
                    "La salle n'est pas disponible le " + dayOfWeek + " (" + dto.getSlotDate() + ")");
        }

        // VALIDATION 7: Le créneau respecte les horaires de la salle
        validateSlotWithinRoomSchedule(room, dto.getStartTime(), dto.getEndTime());

        //  VALIDATION 8: Le médecin a déclaré une disponibilité qui couvre ce créneau
        boolean doctorIsAvailable = !availabilityRepository.findConflictingAvailabilities(
                dto.getDoctorId(),
                room.getHealthCenter().getCenterId(),
                dayOfWeek,
                dto.getStartTime(),
                dto.getEndTime()
        ).isEmpty();
        if (!doctorIsAvailable) {
            throw new IllegalArgumentException(
                    "Le médecin n'a pas déclaré de disponibilité couvrant ce créneau le " + dayOfWeek);
        }

        //VALIDATION 9: Pas de conflit avec les slots existants du médecin (DATE SPÉCIFIQUE)
        if (slotRepository.existsOverlappingSlotForDoctorOnDate(
                dto.getDoctorId(), dto.getSlotDate(),
                dto.getStartTime(), dto.getEndTime(), null)) {
            throw new IllegalArgumentException(
                    "Le médecin a déjà un créneau qui chevauche cet horaire le " + dto.getSlotDate());
        }

        // VALIDATION 10: Pas de conflit avec les slots existants de la salle (DATE SPÉCIFIQUE)
        if (slotRepository.existsOverlappingSlotForRoomOnDate(
                dto.getRoomId(), dto.getSlotDate(),
                dto.getStartTime(), dto.getEndTime(), null)) {
            throw new IllegalArgumentException(
                    "La salle est déjà réservée sur cet horaire le " + dto.getSlotDate());
        }

        // VALIDATION 11 (si récurrent): Pas de conflit avec les créneaux récurrents
        if (dto.getIsRecurring() != null && dto.getIsRecurring()) {
            if (slotRepository.existsOverlappingRecurringSlotForDoctor(
                    dto.getDoctorId(), dayOfWeek,
                    dto.getStartTime(), dto.getEndTime(), null)) {
                throw new IllegalArgumentException(
                        "Le médecin a déjà un créneau récurrent qui chevauche cet horaire tous les " + dayOfWeek);
            }

            if (slotRepository.existsOverlappingRecurringSlotForRoom(
                    dto.getRoomId(), dayOfWeek,
                    dto.getStartTime(), dto.getEndTime(), null)) {
                throw new IllegalArgumentException(
                        "La salle a déjà un créneau récurrent qui chevauche cet horaire tous les " + dayOfWeek);
            }
        }

        // CRÉATION du slot
        Slot slot = new Slot();
        slot.setSlotDate(dto.getSlotDate());
        slot.setDayOfWeek(dayOfWeek);
        slot.setStartTime(dto.getStartTime());
        slot.setEndTime(dto.getEndTime());
        slot.setStatus(dto.getStatus() != null ? dto.getStatus() : SlotStatus.AVAILABLE);
        slot.setIsRecurring(dto.getIsRecurring() != null ? dto.getIsRecurring() : false);
        slot.setDoctor(doctor);
        slot.setRoom(room);

        Slot savedSlot = slotRepository.save(slot);
        return convertToDTO(savedSlot);
    }
    // ====================================================================
    // ✏️ MISE À JOUR D'UN CRÉNEAU
    // ====================================================================
    @Override
    @Transactional
    public SlotDTO updateSlot(Long slotId, SlotDTO dto) {
        Slot existing = slotRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Créneau non trouvé avec l'ID: " + slotId));

        // Validation des heures si modifiées
        if (dto.getStartTime() != null && dto.getEndTime() != null) {
            validateTimeRange(dto.getStartTime(), dto.getEndTime());
        }

        // Validation de la date si modifiée
        if (dto.getSlotDate() != null && dto.getSlotDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Impossible de déplacer un créneau dans le passé");
        }

        // Mise à jour des champs si fournis
        if (dto.getSlotDate() != null) {
            existing.setSlotDate(dto.getSlotDate());
            existing.setDayOfWeek(convertToDayOfWeek(dto.getSlotDate().getDayOfWeek()));
        }
        if (dto.getStartTime() != null) existing.setStartTime(dto.getStartTime());
        if (dto.getEndTime() != null) existing.setEndTime(dto.getEndTime());
        if (dto.getStatus() != null) existing.setStatus(dto.getStatus());
        if (dto.getIsRecurring() != null) existing.setIsRecurring(dto.getIsRecurring());

        // Vérifier disponibilité du médecin
        boolean doctorIsAvailable = !availabilityRepository.findConflictingAvailabilities(
                existing.getDoctor().getDoctorId(),
                existing.getRoom().getHealthCenter().getCenterId(),
                existing.getDayOfWeek(),
                existing.getStartTime(),
                existing.getEndTime()
        ).isEmpty();
        if (!doctorIsAvailable) {
            throw new IllegalArgumentException(
                    "Le médecin n'a pas de disponibilité couvrant ce créneau");
        }

        // Vérifier les conflits (en excluant le slot actuel)
        if (slotRepository.existsOverlappingSlotForDoctorOnDate(
                existing.getDoctor().getDoctorId(), existing.getSlotDate(),
                existing.getStartTime(), existing.getEndTime(), slotId)) {
            throw new IllegalArgumentException("Conflit avec un autre créneau du médecin");
        }

        if (slotRepository.existsOverlappingSlotForRoomOnDate(
                existing.getRoom().getRoomId(), existing.getSlotDate(),
                existing.getStartTime(), existing.getEndTime(), slotId)) {
            throw new IllegalArgumentException("Conflit avec un autre créneau de la salle");
        }

        // VALIDATION : Pas de conflit si le créneau est récurrent
        if (existing.getIsRecurring() != null && existing.getIsRecurring()) {
           // si true : on exécute ce code
            if (slotRepository.existsOverlappingRecurringSlotForDoctor(
                    existing.getDoctor().getDoctorId(),
                    existing.getDayOfWeek(),
                    existing.getStartTime(),
                    existing.getEndTime(),
                    slotId)) {
                throw new IllegalArgumentException(
                        "Le médecin a déjà un créneau récurrent qui chevauche cet horaire tous les " + existing.getDayOfWeek());
            }

            if (slotRepository.existsOverlappingRecurringSlotForRoom(
                    existing.getRoom().getRoomId(),
                    existing.getDayOfWeek(),
                    existing.getStartTime(),
                    existing.getEndTime(),
                    slotId)) {
                throw new IllegalArgumentException(
                        "La salle a déjà un créneau récurrent qui chevauche cet horaire tous les " + existing.getDayOfWeek());
            }
        }


        Slot updated = slotRepository.save(existing);
        return convertToDTO(updated);
    }


     @Override
    @Transactional
    public SlotDTO updateSlotStatus(Long slotId, SlotStatus status) {
        Slot existing = slotRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Créneau non trouvé avec l'ID: " + slotId));

        existing.setStatus(status);
        Slot updated = slotRepository.save(existing);
        return convertToDTO(updated);
    }

    // ====================================================================
    // 📋 RÉCUPÉRATION DES CRÉNEAUX
    // ====================================================================
    @Override
    public SlotDTO getSlotById(Long slotId) {
        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Créneau non trouvé avec l'ID: " + slotId));
        return convertToDTO(slot);
    }

    @Override
    public List<SlotDTO> getSlotsByDoctor(Long doctorId) {
        return slotRepository.findByDoctor_DoctorIdOrderBySlotDateAscStartTimeAsc(doctorId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<SlotDTO> getSlotsByRoom(Long roomId) {
        return slotRepository.findByRoom_RoomIdOrderBySlotDateAscStartTimeAsc(roomId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<SlotDTO> getSlotsByStatus(SlotStatus status) {
        return slotRepository.findByStatusOrderBySlotDateAscStartTimeAsc(status)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<SlotDTO> getAllSlots() {
        return slotRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SlotDTO> getSlotsByHealthCenter(Long healthCenterId) {
        return slotRepository.findByRoom_HealthCenter_CenterIdOrderBySlotDateAscStartTimeAsc(healthCenterId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<SlotDTO> getAvailableSlotsBySpecialty(MedicalSpecialty specialty) {
        return slotRepository.findAvailableSlotsBySpecialty(specialty)
                .stream()
                .map(this::convertToDTO) // conversion Slot → SlotDTO
                .collect(Collectors.toList());
    }


    // ====================================================================
    // 🗑️ SUPPRESSION D'UN CRÉNEAU
    // ====================================================================
    @Transactional
    @Override
    public void deleteSlot(Long slotId) {
        if (!slotRepository.existsById(slotId)) {
            throw new IllegalArgumentException("Créneau non trouvé avec l'ID: " + slotId);
        }
        slotRepository.deleteById(slotId);
    }

    // ====================================================================
    // 🔧 MÉTHODES UTILITAIRES PRIVÉES
    // ====================================================================


     // verification: 3 Valider la plage horaire
    private void validateTimeRange(LocalTime start, LocalTime end) {
        if (start.isAfter(end) || start.equals(end)) {
            throw new IllegalArgumentException(
                    "L'heure de début doit être antérieure à l'heure de fin");
        }
    }


     // Vérification 7:  que le créneau demandé (start-end) est bien compris dans , les horaires d'ouverture (time slots) définis pour la salle
     private void validateSlotWithinRoomSchedule(Room room, LocalTime start, LocalTime end) {

         // Si la salle n'a pas d'horaires définis, on ne fait aucune vérification
         if (room.getDefaultTimeSlots() == null || room.getDefaultTimeSlots().isEmpty()) {
             return;
         }

         // Vérifie si le créneau demandé est inclus dans au moins un des créneaux horaires de la salle , .anyMatch(...) → retourne true dès qu’une correspondance est trouvée.
         boolean isWithinSchedule = room.getDefaultTimeSlots().stream()
                 .anyMatch(ts ->
                         !start.isBefore(ts.getStartTime()) &&   // le début du créneau est après (ou égal à) l’ouverture
                                 !end.isAfter(ts.getEndTime())           // la fin du créneau est avant (ou égale à) la fermeture
                 );

         // Si le créneau ne correspond à aucun horaire valide, on lève une exception
         if (!isWithinSchedule) {
             throw new IllegalArgumentException(
                     "Le créneau n'est pas dans les horaires d'ouverture de la salle");
         }
     }

    private DayOfWeek convertToDayOfWeek(java.time.DayOfWeek javaDayOfWeek) {
        return switch (javaDayOfWeek) {
            case MONDAY -> DayOfWeek.MONDAY;
            case TUESDAY -> DayOfWeek.TUESDAY;
            case WEDNESDAY -> DayOfWeek.WEDNESDAY;
            case THURSDAY -> DayOfWeek.THURSDAY;
            case FRIDAY -> DayOfWeek.FRIDAY;
            case SATURDAY -> DayOfWeek.SATURDAY;
            case SUNDAY -> DayOfWeek.SUNDAY;
        };
    }

    private SlotDTO convertToDTO(Slot slot) {
        SlotDTO dto = new SlotDTO();
        dto.setSlotId(slot.getSlotId());
        dto.setSlotDate(slot.getSlotDate());
        dto.setStartTime(slot.getStartTime());
        dto.setEndTime(slot.getEndTime());
        dto.setStatus(slot.getStatus());
        dto.setIsRecurring(slot.getIsRecurring());
        dto.setDoctorId(slot.getDoctor().getDoctorId());
        dto.setRoomId(slot.getRoom().getRoomId());
        dto.setDoctorFirstName(slot.getDoctor().getFirstName());
        dto.setDoctorLastName(slot.getDoctor().getLastName());
        dto.setDoctorSpecialty(slot.getDoctor().getSpecialty().toString());
        dto.setRoomName(slot.getRoom().getName());
        dto.setHealthCenterName(slot.getRoom().getHealthCenter().getName());
        dto.setCreatedAt(slot.getCreatedAt());
        dto.setUpdatedAt(slot.getUpdatedAt());
        return dto;
    }
}