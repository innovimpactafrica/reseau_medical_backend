package com.example.rml.back_office_rml.controllers;

import com.example.rml.back_office_rml.dto.SlotDTO;
import com.example.rml.back_office_rml.enums.MedicalSpecialty;
import com.example.rml.back_office_rml.enums.SlotStatus;
import com.example.rml.back_office_rml.services.SlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/slots")
public class SlotController {

    private final SlotService slotService;

    public SlotController(SlotService slotService) {
        this.slotService = slotService;
    }

    // ============================================================================
    // 📦 CLASSE INTERNE - RÉPONSE D'ERREUR
    // ============================================================================
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

    // ============================================================================
    // 🆕 ENDPOINT - CRÉER UN CRÉNEAU
    // ============================================================================
    @Operation(summary = "Create a new slot (assign doctor to room)",
            description = "The health center assigns a doctor to a room for a specific date and time")
    @PostMapping
    public ResponseEntity<?> createSlot(
            @Parameter(description = "Room ID", required = true)
            @RequestParam Long roomId,

            @Parameter(description = "Doctor ID", required = true)
            @RequestParam Long doctorId,

            @Parameter(description = "Slot date (dd-MM-yyyy)", required = true)
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate slotDate,

            @Parameter(description = "Start time (HH:mm)", required = true)
            @RequestParam String startTimeStr,

            @Parameter(description = "End time (HH:mm)", required = true)
            @RequestParam String endTimeStr,

            @Parameter(description = "Slot status")
            @RequestParam(required = false, defaultValue = "AVAILABLE") SlotStatus status,

            @Parameter(description = "Is recurring (every week)")
            @RequestParam(required = false, defaultValue = "false") Boolean isRecurring
    ) {
        try {
            // Conversion des Strings en LocalTime
            LocalTime startTime = LocalTime.parse(startTimeStr);
            LocalTime endTime = LocalTime.parse(endTimeStr);

            SlotDTO dto = new SlotDTO();
            dto.setRoomId(roomId);
            dto.setDoctorId(doctorId);
            dto.setSlotDate(slotDate);
            dto.setStartTime(startTime);
            dto.setEndTime(endTime);
            dto.setStatus(status);
            dto.setIsRecurring(isRecurring);

            SlotDTO result = slotService.createSlot(dto);
            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("VALIDATION_ERROR", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "Erreur : " + e.getMessage()));
        }
    }

    // ============================================================================
    // ✏️ ENDPOINT - METTRE À JOUR UN CRÉNEAU
    // ============================================================================
    @Operation(summary = "Update an existing slot")
    @PutMapping("/{slotId}")
    public ResponseEntity<?> updateSlot(
            @PathVariable Long slotId,

            @Parameter(description = "Slot date (dd-MM-yyyy)")
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate slotDate,

            @Parameter(description = "Start time (HH:mm)" )
            @RequestParam (required = false)  String startTimeStr ,

            @Parameter(description = "End time (HH:mm)")
            @RequestParam (required = false)  String endTimeStr,

            @Parameter(description = "Slot status")
            @RequestParam(required = false) SlotStatus status,

            @Parameter(description = "Is recurring")
            @RequestParam(required = false) Boolean isRecurring
    ) {
        try {
            // Conversion des Strings en LocalTime
            LocalTime startTime = null;
            LocalTime endTime = null;

            if (startTimeStr != null && endTimeStr != null) {
                startTime = LocalTime.parse(startTimeStr);
                endTime = LocalTime.parse(endTimeStr);
            }

            SlotDTO dto = new SlotDTO();
            dto.setSlotDate(slotDate);
            dto.setStartTime(startTime);
            dto.setEndTime(endTime);
            dto.setStatus(status);
            dto.setIsRecurring(isRecurring);

            SlotDTO result = slotService.updateSlot(slotId, dto);
            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("VALIDATION_ERROR", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "Erreur : " + e.getMessage()));
        }
    }

    // ============================================================================
    // 🔄 ENDPOINT - METTRE À JOUR LE STATUT
    // ============================================================================
    @Operation(summary = "Update slot status only")
    @PatchMapping("/{slotId}/status")
    public ResponseEntity<?> updateSlotStatus(
            @PathVariable Long slotId,
            @Parameter(description = "New status", required = true)
            @RequestParam SlotStatus status
    ) {
        try {
            SlotDTO result = slotService.updateSlotStatus(slotId, status);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("VALIDATION_ERROR", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "Erreur : " + e.getMessage()));
        }
    }

    // ============================================================================
    // 🗑️ ENDPOINT - SUPPRIMER UN CRÉNEAU
    // ============================================================================
    @Operation(summary = "Delete a slot")
    @DeleteMapping("/{slotId}")
    public ResponseEntity<?> deleteSlot(@PathVariable Long slotId) {
        try {
            slotService.deleteSlot(slotId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("NOT_FOUND", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "Erreur : " + e.getMessage()));
        }
    }

    // ============================================================================
    // 📋 ENDPOINTS - RÉCUPÉRATION
    // ============================================================================
    @Operation(summary = "Get slot by ID")
    @GetMapping("/{slotId}")
    public ResponseEntity<?> getSlotById(@PathVariable Long slotId) {
        try {
            SlotDTO slot = slotService.getSlotById(slotId);
            return ResponseEntity.ok(slot);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("NOT_FOUND", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "Erreur : " + e.getMessage()));
        }
    }

    @Operation(summary = "Get all slots")
    @GetMapping
    public ResponseEntity<?> getAllSlots() {
        try {
            List<SlotDTO> slots = slotService.getAllSlots();
            return ResponseEntity.ok(slots);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "Erreur : " + e.getMessage()));
        }
    }

    @Operation(summary = "Get all slots for a doctor")
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<?> getSlotsByDoctor(@PathVariable Long doctorId) {
        try {
            List<SlotDTO> slots = slotService.getSlotsByDoctor(doctorId);
            return ResponseEntity.ok(slots);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "Erreur : " + e.getMessage()));
        }
    }

    @Operation(summary = "Get all slots for a room")
    @GetMapping("/room/{roomId}")
    public ResponseEntity<?> getSlotsByRoom(@PathVariable Long roomId) {
        try {
            List<SlotDTO> slots = slotService.getSlotsByRoom(roomId);
            return ResponseEntity.ok(slots);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "Erreur : " + e.getMessage()));
        }
    }

    @Operation(summary = "Get all slots for a health center")
    @GetMapping("/healthcenter/{healthCenterId}")
    public ResponseEntity<?> getSlotsByHealthCenter(@PathVariable Long healthCenterId) {
        try {
            List<SlotDTO> slots = slotService.getSlotsByHealthCenter(healthCenterId);
            return ResponseEntity.ok(slots);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "Erreur : " + e.getMessage()));
        }
    }

    @Operation(summary = "Get slots by status")
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getSlotsByStatus(@PathVariable SlotStatus status) {
        try {
            List<SlotDTO> slots = slotService.getSlotsByStatus(status);
            return ResponseEntity.ok(slots);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "Erreur : " + e.getMessage()));
        }
    }

    @Operation(summary = "Get available slots by medical specialty")
    @GetMapping("/available/specialty/{specialty}")
    public ResponseEntity<?> getAvailableSlotsBySpecialty(@PathVariable MedicalSpecialty specialty) {
        try {
            List<SlotDTO> slots = slotService.getAvailableSlotsBySpecialty(specialty);
            return ResponseEntity.ok(slots);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "Erreur : " + e.getMessage()));
        }
    }
}