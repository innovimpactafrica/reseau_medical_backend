package com.example.rml.back_office_rml.controllers;

import com.example.rml.back_office_rml.dto.DoctorAvailabilityDTO;
import com.example.rml.back_office_rml.enums.DayOfWeek;
import com.example.rml.back_office_rml.services.DoctorAvailabilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/doctor-availabilities")
public class DoctorAvailabilityController {

    private final DoctorAvailabilityService availabilityService;

    public DoctorAvailabilityController(DoctorAvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    // ============================================================================
    // 🆕 ENDPOINT -CRÉER UNE DISPONIBILITÉ MÉDECIN
    // ============================================================================
    @Operation(summary = "Create a new availability for a doctor",
            description = "The doctor declares their availability in a health center")
    @PostMapping
    public ResponseEntity<?> createAvailability(
            // INFO MÉDECIN ET CENTRE
            @Parameter(description = "Doctor ID", required = true)
            @RequestParam Long doctorId,

            @Parameter(description = "Health center ID", required = true)
            @RequestParam Long healthCenterId,

            // JOUR ET HORAIRES
            @Parameter(description = "Day of the week", required = true)
            @RequestParam DayOfWeek dayOfWeek,

            @Parameter(description = "Start time (format: HH:mm)", required = true)
            @RequestParam @DateTimeFormat(pattern = "HH:mm") String startTime,

            @Parameter(description = "End time (format: HH:mm)", required = true)
            @RequestParam @DateTimeFormat(pattern = "HH:mm") String endTime,

            // CONFIGURATION DES CONSULTATIONS
            @Parameter(description = "Consultation duration ID", required = true)
            @RequestParam Long consultationDuration_id,

            @Parameter(description = "Recurring (every week)")
            @RequestParam(defaultValue = "true") Boolean isRecurring
    ) {
        try {
            // Conversion des heures de String vers LocalTime
            LocalTime start = LocalTime.parse(startTime);
            LocalTime end = LocalTime.parse(endTime);

            // Construire le DTO pour le service
            DoctorAvailabilityDTO dto = new DoctorAvailabilityDTO();
            dto.setDoctorId(doctorId);
            dto.setHealthCenterId(healthCenterId);
            dto.setDayOfWeek(dayOfWeek);
            dto.setStartTime(start);
            dto.setEndTime(end);
            dto.setConsultationDuration_Id(consultationDuration_id);
            dto.setIsRecurring(isRecurring);

            // Appel du service pour créer la disponibilité
            DoctorAvailabilityDTO result = availabilityService.createAvailability(dto);
            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            // Erreurs métier
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("BUSINESS_ERROR", e.getMessage()));

        } catch (Exception e) {
            // Erreurs internes
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR",
                            "Error while creating availability: " + e.getMessage()));
        }
    }

    // ============================================================================
    // 📋 ENDPOINTS -LIRE LES DISPONIBILITÉS
    // ============================================================================
    @Operation(summary = "Get all availabilities of a doctor",
            description = "Returns the list of all time slots for the doctor")
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<?> getAvailabilitiesByDoctor(
            @Parameter(description = "Doctor ID")
            @PathVariable Long doctorId
    ) {
        try {
            List<DoctorAvailabilityDTO> availabilities =
                    availabilityService.getAvailabilitiesByDoctor(doctorId);
            return ResponseEntity.ok(availabilities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR",
                            "Error while retrieving availabilities: " + e.getMessage()));
        }
    }

    @Operation(summary = "Get doctors available in a health center",
            description = "Returns all doctors available in a health center")
    @GetMapping("/healthcenter/{healthCenterId}")
    public ResponseEntity<?> getAvailabilitiesByHealthCenter(
            @Parameter(description = "Health center ID")
            @PathVariable Long healthCenterId
    ) {
        try {
            List<DoctorAvailabilityDTO> availabilities =
                    availabilityService.getAvailabilitiesByHealthCenter(healthCenterId);
            return ResponseEntity.ok(availabilities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR",
                            "Error while retrieving availabilities by health center: " + e.getMessage()));
        }
    }

    @Operation(summary = "Get availability by ID")
    @GetMapping("/{availabilityId}")
    public ResponseEntity<?> getAvailabilityById(
            @Parameter(description = "Availability ID")
            @PathVariable Long availabilityId
    ) {
        try {
            DoctorAvailabilityDTO availability =
                    availabilityService.getAvailabilityById(availabilityId);
            return ResponseEntity.ok(availability);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("NOT_FOUND", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", e.getMessage()));
        }
    }

    // ============================================================================
    // ✏️ ENDPOINT - MODIFIER UNE DISPONIBILITÉ
    // ============================================================================
    @Operation(summary = "Update an existing availability")
    @PutMapping("/{availabilityId}")
    public ResponseEntity<?> updateAvailability(
            @PathVariable Long availabilityId,

            @RequestParam(required = false) Long healthCenterId,

            @Parameter(description = "New day of the week")
            @RequestParam(required = false) DayOfWeek dayOfWeek,

            @Parameter(description = "Start time (format: HH:mm)")
            @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm") String startTime,

            @Parameter(description = "End time (format: HH:mm)")
            @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm") String endTime,

            @Parameter(description = "New consultation duration ID")
            @RequestParam(required = false) Long consultationDurationId,

            @Parameter(description = "New recurring status")
            @RequestParam(required = false) Boolean isRecurring
    ) {
        try {
            // Conversion des heures si fournies
            LocalTime start = startTime != null ? LocalTime.parse(startTime) : null;
            LocalTime end = endTime != null ? LocalTime.parse(endTime) : null;

            // Construire le DTO pour le service
            DoctorAvailabilityDTO dto = new DoctorAvailabilityDTO();
            if (healthCenterId != null) dto.setHealthCenterId(healthCenterId);
            if (dayOfWeek != null) dto.setDayOfWeek(dayOfWeek);
            if (start != null) dto.setStartTime(start);
            if (end != null) dto.setEndTime(end);
            if (consultationDurationId != null) dto.setConsultationDuration_Id(consultationDurationId);
            if (isRecurring != null) dto.setIsRecurring(isRecurring);

            DoctorAvailabilityDTO result =
                    availabilityService.updateAvailability(availabilityId, dto);
            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("VALIDATION_ERROR", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR",
                            "Error while updating availability: " + e.getMessage()));
        }
    }

    // ============================================================================
    // 🔄 ENDPOINT - ACTIVER/DÉSACTIVER UNE DISPONIBILITÉ
    // ============================================================================
    @Operation(summary = "Activate/deactivate an availability",
            description = "Temporarily disable a time slot (vacation, unavailability)")
    @PatchMapping("/{availabilityId}/toggle")
    public ResponseEntity<?> toggleAvailabilityStatus(
            @Parameter(description = "Availability ID")
            @PathVariable Long availabilityId
    ) {
        try {
            DoctorAvailabilityDTO result =
                    availabilityService.toggleAvailabilityStatus(availabilityId);
            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("NOT_FOUND", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", e.getMessage()));
        }
    }

    // ============================================================================
    // 📋 ENDPOINT - OBTENIR LES DISPONIBILITÉS ACTIVES D’UN MÉDECIN
    // ============================================================================
    @Operation(summary = "Get active availabilities of a doctor",
            description = "Returns the list of active time slots for a given doctor")
    @GetMapping("/doctor/{doctorId}/active")
    public ResponseEntity<?> getActiveAvailabilitiesByDoctor(
            @Parameter(description = "Doctor ID", required = true)
            @PathVariable Long doctorId
    ) {
        try {
            List<DoctorAvailabilityDTO> activeAvailabilities =
                    availabilityService.getActiveAvailabilitiesByDoctor(doctorId);
            return ResponseEntity.ok(activeAvailabilities);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR",
                            "Error while retrieving active availabilities: " + e.getMessage()));
        }
    }

    // ============================================================================
    // 🗑️ ENDPOINT - SUPPRIMER UNE DISPONIBILITÉ
    // ============================================================================
    @Operation(summary = "Delete an availability")
    @DeleteMapping("/{availabilityId}")
    public ResponseEntity<?> deleteAvailability(
            @Parameter(description = "Availability ID")
            @PathVariable Long availabilityId
    ) {
        try {
            availabilityService.deleteAvailability(availabilityId);
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("NOT_FOUND", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR",
                            "Error while deleting availability: " + e.getMessage()));
        }
    }

    // ============================================================================
    // 🎯CLASSE DE RÉPONSE D’ERREUR
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
}
