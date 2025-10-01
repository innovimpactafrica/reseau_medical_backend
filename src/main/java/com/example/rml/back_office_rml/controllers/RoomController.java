package com.example.rml.back_office_rml.controllers;

import com.example.rml.back_office_rml.dto.DefaultTimeSlotDTO;
import com.example.rml.back_office_rml.dto.RoomDTO;
import com.example.rml.back_office_rml.entities.Room;
import com.example.rml.back_office_rml.enums.DayOfWeek;
import com.example.rml.back_office_rml.enums.RoomStatus;
import com.example.rml.back_office_rml.repositories.RoomRepository;
import com.example.rml.back_office_rml.services.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;
    private final RoomRepository roomRepository;

    // ============================================================================
    // 🏗️ CONSTRUCTEUR - INJECTION DE DÉPENDANCES
    // ============================================================================

    /**
     * Constructeur avec injection de dépendances
     */
    public RoomController(RoomService roomService , RoomRepository roomRepository) {
        this.roomService = roomService;
        this.roomRepository=roomRepository;

    }

    // ============================================================================
    // 📦 CLASSES INTERNES - STRUCTURES DE DONNÉES
    // ============================================================================

    /**
     * Classe pour standardiser les réponses d'erreur de l'API
     */
    @Getter
    public static class ErrorResponse {
        private final String error;
        private final String message;
        private final long timestamp;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }
    }

    /**
     * Classe pour encapsuler le résultat du traitement des créneaux horaires
     * Permet de retourner soit les créneaux valides, soit une erreur détaillée
     */
    @Getter
    public static class TimeSlotProcessingResult {
        private final List<DefaultTimeSlotDTO> timeSlots;
        private final ErrorResponse error;

        /**
         * Constructeur pour un résultat positif (créneaux valides)
         */
        public TimeSlotProcessingResult(List<DefaultTimeSlotDTO> timeSlots) {
            this.timeSlots = timeSlots;
            this.error = null;
        }

        /**
         * Constructeur pour un résultat négatif (erreur de traitement)
         */
        public TimeSlotProcessingResult(ErrorResponse error) {
            this.timeSlots = null;
            this.error = error;
        }

        /**
         * Vérifie si le résultat contient une erreur
         */
        public boolean hasError() {
            return error != null;
        }
    }

    // ============================================================================
    // 🆕 ENDPOINT - CRÉATION D'UNE SALLE
    // ============================================================================

    @Operation(summary = "Create a new room in a health center",
            description = "Create a new room with availability days and time slots")
    @PostMapping("/{healthCenterId}")
    public ResponseEntity<?> createRoom(
            // INFORMATIONS DE BASE DE LA SALLE
            @Parameter(description = "Room name", required = true)
            @RequestParam String name,

            @Parameter(description = "Room capacity (maximum number of people)")
            @RequestParam(required = false) Integer capacity,

            @Parameter(description = "Room status", required = true)
            @RequestParam RoomStatus status,

            @Parameter(description = "Associated health center ID", required = true)
            @PathVariable Long healthCenterId,

            // JOURS DE DISPONIBILITÉ (CASES À COCHER)
            @Parameter(description = "Available on Monday")
            @RequestParam(defaultValue = "false") Boolean monday,

            @Parameter(description = "Available on Tuesday")
            @RequestParam(defaultValue = "false") Boolean tuesday,

            @Parameter(description = "Available on Wednesday")
            @RequestParam(defaultValue = "false") Boolean wednesday,

            @Parameter(description = "Available on Thursday")
            @RequestParam(defaultValue = "false") Boolean thursday,

            @Parameter(description = "Available on Friday")
            @RequestParam(defaultValue = "false") Boolean friday,

            @Parameter(description = "Available on Saturday")
            @RequestParam(defaultValue = "false") Boolean saturday,

            @Parameter(description = "Available on Sunday")
            @RequestParam(defaultValue = "false") Boolean sunday,

            // CRÉNEAUX HORAIRES (CHAÎNES DE CARACTÈRES)
            @Parameter(description = "Start times (format: HH:mm, separated by comma)")
            @RequestParam(required = false) String startTimes,

            @Parameter(description = "End times (format: HH:mm, separated by comma)")
            @RequestParam(required = false) String endTimes
    ) {
        try {
            // ÉTAPE 1 : CONSTRUIRE LA LISTE DES JOURS SÉLECTIONNÉS
            Set<DayOfWeek> selectedDays = buildSelectedDaysAdd(monday, tuesday, wednesday,
                    thursday, friday, saturday, sunday);

            // Validation : au moins un jour doit être sélectionné pour un 'ajout'
            if (selectedDays.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("VALIDATION_ERROR",
                                "Aucun jour sélectionné pour la salle. Veuillez sélectionner au moins un jour."));
            }

            // ÉTAPE 2 : TRAITEMENT DES CRÉNEAUX HORAIRES (timeSlots =>timeSlotResult )
            TimeSlotProcessingResult timeSlotResult = processTimeSlots(startTimes, endTimes);
            if (timeSlotResult.hasError()) {
                return ResponseEntity.badRequest().body(timeSlotResult.getError());
            }

            // ÉTAPE 3 : CONSTRUIRE LE DTO DE LA SALLE
            RoomDTO roomDTO = buildRoomDTO(name, capacity, status, healthCenterId,
                    selectedDays, timeSlotResult.getTimeSlots());

            // ÉTAPE 4 : APPELER LE SERVICE DE CRÉATION
            RoomDTO createdRoom = roomService.createRoom(roomDTO);
            return ResponseEntity.ok(createdRoom);

        } catch (IllegalArgumentException e) {
            // Erreurs métier (centre non trouvé, nom déjà existant, etc.)
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("BUSINESS_ERROR", e.getMessage()));

        } catch (Exception e) {
            // Erreurs internes non prévues
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR",
                            "Une erreur inattendue s'est produite : " + e.getMessage()));
        }
    }

    // ============================================================================
    // ✏️ ENDPOINT - MISE À JOUR D'UNE SALLE
    // ============================================================================

    @Operation(summary = "Update an existing room",
            description = "Update room information including days and time slots")
    @PutMapping("/{roomId}")
    public ResponseEntity<?> updateRoom(
            @PathVariable Long roomId,

            // PARAMÈTRES DE MISE À JOUR (TOUS OPTIONNELS)
            @Parameter(description = "Room name")
            @RequestParam(required = false) String name,

            @Parameter(description = "Room capacity")
            @RequestParam(required = false) Integer capacity,

            @Parameter(description = "Room status")
            @RequestParam(required = false) RoomStatus status,

            @Parameter(description = "Associated health center ID")
            @RequestParam(required = false) Long healthCenterId,

            // JOURS DE DISPONIBILITÉ
            @Parameter(description = "Available on Monday")
            @RequestParam(required = false) Boolean monday,

            @Parameter(description = "Available on Tuesday")
            @RequestParam(required = false) Boolean tuesday,

            @Parameter(description = "Available on Wednesday")
            @RequestParam(required = false) Boolean wednesday,

            @Parameter(description = "Available on Thursday")
            @RequestParam(required = false) Boolean thursday,

            @Parameter(description = "Available on Friday")
            @RequestParam(required = false) Boolean friday,

            @Parameter(description = "Available on Saturday")
            @RequestParam(required = false) Boolean saturday,

            @Parameter(description = "Available on Sunday")
            @RequestParam(required = false) Boolean sunday,

            // CRÉNEAUX HORAIRES
            @Parameter(description = "Start times (format: HH:mm, separated by comma)")
            @RequestParam(required = false) String startTimes,

            @Parameter(description = "End times (format: HH:mm, separated by comma)")
            @RequestParam(required = false) String endTimes
    ) {
        try {

            //  CHARGER LA SALLE EXISTANTE pour les jours de la semaine
            Room existingRoom = roomRepository.findById(roomId)
                    .orElseThrow(() -> new RuntimeException("Salle non trouvée"));

            // ÉTAPE 1: CONSTRUIRE LES JOURS AVEC LES VALEURS EXISTANTES
            Set<DayOfWeek> selectedDays = buildSelectedDaysUpdate(monday, tuesday, wednesday,
                    thursday, friday, saturday, sunday,
                    existingRoom.getAvailableDays());

            // ÉTAPE 2 : TRAITEMENT DES CRÉNEAUX HORAIRES
            // timeSlotResult = retour de processTimeSlots() c'est un objet (contient soit les créneaux valides ( variable timeSlots ), soit une erreur (variable error))
            TimeSlotProcessingResult timeSlotResult = processTimeSlots(startTimes, endTimes);

            // timeSlotResult.getError() = récupère l'erreur ResponseEntity.badRequest().body() = retourne HTTP 400 avec l'erreur
            if (timeSlotResult.hasError()) {
                return ResponseEntity.badRequest().body(timeSlotResult.getError());
            }
            // Si on passe ici → timeSlotResult.hasError() = false  → timeSlotResult.getTimeSlots() = liste des créneaux valides→ La suite du code peut utiliser et accedér au variable via timeSlotResult.getTimeSlots()

            // ÉTAPE 3 : CONSTRUIRE LE DTO DE MISE À JOUR
            RoomDTO roomDTO = buildRoomDTO(name, capacity, status, healthCenterId,
                    selectedDays, timeSlotResult.getTimeSlots());

            // ÉTAPE 4 : APPELER LE SERVICE DE MISE À JOUR
            RoomDTO updatedRoom = roomService.updateRoom(roomId, roomDTO);
            return ResponseEntity.ok(updatedRoom);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("BUSINESS_ERROR", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR",
                            "Erreur lors de la mise à jour : " + e.getMessage()));
        }
    }

    // ============================================================================
    // 📋 ENDPOINTS DE LECTURE
    // ============================================================================

    @Operation(summary = "Get all rooms", description = "Retrieve all rooms from the system")
    @GetMapping
    public ResponseEntity<?> getAllRooms() {
        try {
            List<RoomDTO> rooms = roomService.getAllRooms();
            return ResponseEntity.ok(rooms);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR",
                            "Erreur lors de la récupération des salles : " + e.getMessage()));
        }
    }

    @Operation(summary = "Get rooms by health center",
            description = "Retrieve all rooms belonging to a specific health center")
    @GetMapping("/healthcenter/{healthCenterId}")
    public ResponseEntity<?> getRoomsByHealthCenter(
            @Parameter(description = "Health center ID", required = true)
            @PathVariable Long healthCenterId
    ) {
        try {
            List<RoomDTO> rooms = roomService.getRoomsByHealthCenter(healthCenterId);
            return ResponseEntity.ok(rooms);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("NOT_FOUND", "Erreur: " + e.getMessage()));
        }
    }

    @Operation(summary = "Get room by ID", description = "Retrieve a specific room by its ID")
    @GetMapping("/{roomId}")
    public ResponseEntity<?> getRoomById(
            @Parameter(description = "Room ID") @PathVariable Long roomId
    ) {
        try {
            RoomDTO room = roomService.getRoomById(roomId);
            return ResponseEntity.ok(room);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("NOT_FOUND", "Salle non trouvée : " + e.getMessage()));
        }
    }

    @Operation(
            summary = "Get rooms by status",
            description = "Retrieve the list of rooms filtered by their current status"
    )
    @GetMapping("/status")
    public ResponseEntity<?> getRoomsByStatus(
            @Parameter(description = "Room status ")
            @RequestParam RoomStatus status) {
        try {
            List<RoomDTO> roomDTOList = roomService.getRoomsByStatus(status);
            return ResponseEntity.ok(roomDTOList);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error: " + e.getMessage());
        }
    }

    // ============================================================================
    // 🔧 ENDPOINTS DE GESTION DES CRÉNEAUX HORAIRES
    // ============================================================================

    @Operation(summary = "Add time slot to room",
            description = "Add a new time slot to an existing room")
    @PostMapping("/{roomId}/time-slots")
    public ResponseEntity<?> addTimeSlot(
            @Parameter(description = "Room ID") @PathVariable Long roomId,
            @Parameter(description = "Start time (HH:mm format)") @RequestParam String startTime,
            @Parameter(description = "End time (HH:mm format)") @RequestParam String endTime
    ) {
        try {
            // VALIDATION ET CONVERSION DES HEURES
            LocalTime[] times = validateAndParseTimes(startTime, endTime);
            if (times == null) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("VALIDATION_ERROR",
                                "Heure de début doit être avant l'heure de fin"));
            }

            // CRÉATION DU DTO DU CRÉNEAU
            DefaultTimeSlotDTO timeSlotDTO = new DefaultTimeSlotDTO();
            timeSlotDTO.setStartTime(times[0]);
            timeSlotDTO.setEndTime(times[1]);

            // AJOUT DU CRÉNEAU À LA SALLE
            RoomDTO result = roomService.addDefaultTimeSlot(roomId, timeSlotDTO);
            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("NOT_FOUND", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR",
                            "Erreur lors de l'ajout du créneau : " + e.getMessage()));
        }
    }

    // ============================================================================
    // ⚙️ ENDPOINTS DE GESTION DU STATUT
    // ============================================================================

    @Operation(summary = "Update room status",
            description = "Update only the status of a room")
    @PatchMapping("/{roomId}/status")
    public ResponseEntity<?> updateRoomStatus(
            @PathVariable Long roomId,
            @Parameter(description = "New room status", required = true)
            @RequestParam @NotNull RoomStatus status
    ) {
        try {
            RoomDTO result = roomService.updateRoomStatus(roomId, status);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("UPDATE_ERROR",
                            "Erreur lors de la mise à jour du statut : " + e.getMessage()));
        }
    }

    // ============================================================================
    // 🗑️ ENDPOINT - SUPPRESSION
    // ============================================================================

    @Operation(summary = "Delete a room", description = "Permanently delete a room")
    @DeleteMapping("/{roomId}")
    public ResponseEntity<?> deleteRoom(
            @Parameter(description = "Room ID") @PathVariable Long roomId
    ) {
        try {
            roomService.deleteRoom(roomId);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("DELETE_ERROR",
                            "Erreur lors de la suppression : " + e.getMessage()));
        }
    }


    // ============================================================================
    // 🔧 MÉTHODES UTILITAIRES PRIVÉES
    // ============================================================================

    /**
     * Construit la liste des jours sélectionnés à partir des paramètres booléens
     */
    private Set<DayOfWeek> buildSelectedDaysAdd(Boolean monday, Boolean tuesday, Boolean wednesday,
                                             Boolean thursday, Boolean friday, Boolean saturday,
                                             Boolean sunday) {
        Set<DayOfWeek> selectedDays = new HashSet<>();
        if (monday) selectedDays.add(DayOfWeek.MONDAY);
        if (tuesday) selectedDays.add(DayOfWeek.TUESDAY);
        if (wednesday) selectedDays.add(DayOfWeek.WEDNESDAY);
        if (thursday) selectedDays.add(DayOfWeek.THURSDAY);
        if (friday) selectedDays.add(DayOfWeek.FRIDAY);
        if (saturday) selectedDays.add(DayOfWeek.SATURDAY);
        if (sunday) selectedDays.add(DayOfWeek.SUNDAY);
        return selectedDays;
    }

    /**
     * Construit la liste des jours sélectionnés à partir des paramètres booléens si c'est différent de null
     */

    private Set<DayOfWeek> buildSelectedDaysUpdate(Boolean monday, Boolean tuesday, Boolean wednesday,
                                             Boolean thursday, Boolean friday, Boolean saturday,
                                             Boolean sunday, Set<DayOfWeek> existingDays) {

        // COMMENCER AVEC LES JOURS EXISTANTS
        Set<DayOfWeek> selectedDays = new HashSet<>(existingDays);

        // METTRE À JOUR SEULEMENT LES JOURS FOURNIS
        if (monday != null) {
            if (monday) {
                selectedDays.add(DayOfWeek.MONDAY);  // Ajouter si true
            } else {
                selectedDays.remove(DayOfWeek.MONDAY); // Retirer si false
            }
        }
        // Si monday == null → on ne change rien on va prendre sa valeur des jours existants (existingDays)

        if (tuesday != null) {
            if (tuesday) {
                selectedDays.add(DayOfWeek.TUESDAY);
            } else {
                selectedDays.remove(DayOfWeek.TUESDAY);
            }
        }

        if (wednesday != null) {
            if (wednesday) {
                selectedDays.add(DayOfWeek.WEDNESDAY);
            } else {
                selectedDays.remove(DayOfWeek.WEDNESDAY);
            }
        }

        if (thursday != null) {
            if (thursday) {
                selectedDays.add(DayOfWeek.THURSDAY);
            } else {
                selectedDays.remove(DayOfWeek.THURSDAY);
            }
        }

        if (friday != null) {
            if (friday) {
                selectedDays.add(DayOfWeek.FRIDAY);
            } else {
                selectedDays.remove(DayOfWeek.FRIDAY);
            }
        }

        if (saturday != null) {
            if (saturday) {
                selectedDays.add(DayOfWeek.SATURDAY);
            } else {
                selectedDays.remove(DayOfWeek.SATURDAY);
            }
        }

        if (sunday != null) {
            if (sunday) {
                selectedDays.add(DayOfWeek.SUNDAY);
            } else {
                selectedDays.remove(DayOfWeek.SUNDAY);
            }
        }

        return selectedDays;
    }

    /**
     * Traite et valide les créneaux horaires fournis sous forme de chaînes
     * Retourne un TimeSlotProcessingResult contenant soit les créneaux valides, soit une erreur
     */
    private TimeSlotProcessingResult processTimeSlots(String startTimes, String endTimes) {
        List<DefaultTimeSlotDTO> timeSlots = new ArrayList<>();

        // CAS 1 : Les deux sont vides → return timeSlots vide
        if ((startTimes == null || startTimes.trim().isEmpty()) &&
                (endTimes == null || endTimes.trim().isEmpty())) {
            return new TimeSlotProcessingResult(timeSlots);
            // ⇨ timeSlotResult.hasError() = false
            // ⇨ timeSlotResult.getTimeSlots() = timeSlots (liste vide)
        }

        // CAS 2 : Un seul est rempli → return erreur
        if ((startTimes != null && !startTimes.trim().isEmpty() &&
                (endTimes == null || endTimes.trim().isEmpty())) ||
                (startTimes == null || startTimes.trim().isEmpty()) &&
                        endTimes != null && !endTimes.trim().isEmpty()) {
            return new TimeSlotProcessingResult(
                    new ErrorResponse("VALIDATION_ERROR", "...")
            );
            // ⇨ timeSlotResult.hasError() = true
            // ⇨ timeSlotResult.getError() = cet ErrorResponse
        }

        // CAS 3 : Les deux sont remplis
        String[] startTimeArray = startTimes.split(",");
        String[] endTimeArray = endTimes.split(",");

        // Longueurs différentes → return erreur
        if (startTimeArray.length != endTimeArray.length) {
            return new TimeSlotProcessingResult(
                    new ErrorResponse("VALIDATION_ERROR", "...")
            );
            // ⇨ timeSlotResult.hasError() = true
        }

        for (int i = 0; i < startTimeArray.length; i++) {
            try {
                LocalTime startTime = LocalTime.parse(startTimeArray[i].trim());
                LocalTime endTime = LocalTime.parse(endTimeArray[i].trim());

                // Heure début >= heure fin → return erreur
                if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
                    return new TimeSlotProcessingResult(
                            new ErrorResponse("VALIDATION_ERROR", "...")
                    );
                    // ⇨ timeSlotResult.hasError() = true
                }

                // Création créneau valide
                DefaultTimeSlotDTO timeSlotDTO = new DefaultTimeSlotDTO();
                timeSlotDTO.setStartTime(startTime);
                timeSlotDTO.setEndTime(endTime);
                timeSlots.add(timeSlotDTO);

            } catch (DateTimeParseException e) {
                // Format heure invalide → return erreur
                return new TimeSlotProcessingResult(
                        new ErrorResponse("FORMAT_ERROR", "...")
                );
                // ⇨ timeSlotResult.hasError() = true
            }
        }

        // Tout est valide → return timeSlots
        return new TimeSlotProcessingResult(timeSlots);
        // ⇨ timeSlotResult.hasError() = false
        // ⇨ timeSlotResult.getTimeSlots() = timeSlots (liste pleine)
    }

    /**
     * Construit un DTO RoomDTO à partir des paramètres fournis
     */
    private RoomDTO buildRoomDTO(String name, Integer capacity, RoomStatus status,
                                 Long healthCenterId, Set<DayOfWeek> availableDays,
                                 List<DefaultTimeSlotDTO> timeSlots) {
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setName(name);
        roomDTO.setCapacity(capacity);
        roomDTO.setStatus(status);
        roomDTO.setHealthCenterId(healthCenterId);
        roomDTO.setAvailableDays(availableDays);
        roomDTO.setDefaultTimeSlotsDto(timeSlots);
        return roomDTO;
    }

    /**
     * Valide et parse les heures de début et fin pour l'ajout des horaires séparemment
     * Retourne un tableau [startTime, endTime] ou null si validation échoue
     */
    private LocalTime[] validateAndParseTimes(String startTimeStr, String endTimeStr) {
        try {
            LocalTime startTime = LocalTime.parse(startTimeStr);
            LocalTime endTime = LocalTime.parse(endTimeStr);

            if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
                throw new RuntimeException("L'heure de début  doit être antérieure à l'heure de fin  pour le créneau ");
            }

            return new LocalTime[]{startTime, endTime};

        } catch (DateTimeParseException e) {
            return null;
        }
    }
}