package com.example.rml.back_office_rml.controllers;

import com.example.rml.back_office_rml.dto.ConsultationDurationDTO;
import com.example.rml.back_office_rml.services.ConsultationDurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/consultation-durations")
public class ConsultationDurationController {

    private final ConsultationDurationService durationService;

    public ConsultationDurationController(ConsultationDurationService durationService) {
        this.durationService = durationService;
    }

    // ============================================================================
    // 🆕 ENDPOINT - CRÉATION D'UNE DURÉE
    // ============================================================================
    @Operation(summary = "Create a new consultation duration",
            description = "Add a new duration available for consultations")
    @PostMapping
    public ResponseEntity<?> createDuration(
            @Parameter(description = "Duration in minutes", required = true)
            @RequestParam Integer minutes,

            @Parameter(description = "Display name", required = true)
            @RequestParam String displayName) {

        try {
            ConsultationDurationDTO durationDTO = new ConsultationDurationDTO();
            durationDTO.setMinutes(minutes);
            durationDTO.setDisplayName(displayName);

            ConsultationDurationDTO created = durationService.createDuration(durationDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("VALIDATION_ERROR", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR",
                            "Error while creating duration: " + e.getMessage()));
        }
    }

    // ============================================================================
    // 📋 ENDPOINTS DE LECTURE
    // ============================================================================
    @Operation(summary = "Get all consultation durations",
            description = "Returns the complete list of available consultation durations")
    @GetMapping
    public ResponseEntity<List<ConsultationDurationDTO>> getAllDurations() {
        try {
            List<ConsultationDurationDTO> durations = durationService.getAllDurations();
            return ResponseEntity.ok(durations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Get active consultation durations",
            description = "Returns only the active durations available for use")
    @GetMapping("/active")
    public ResponseEntity<List<ConsultationDurationDTO>> getActiveDurations() {
        try {
            List<ConsultationDurationDTO> durations = durationService.getActiveDurations();
            return ResponseEntity.ok(durations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Get consultation duration by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getDurationById(
            @Parameter(description = "Duration ID")
            @PathVariable Long id
    ) {
        try {
            Optional<ConsultationDurationDTO> duration = durationService.getDurationById(id);
            return duration.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", e.getMessage()));
        }
    }

    @Operation(summary = "Get consultation duration by minutes")
    @GetMapping("/by-minutes/{minutes}")
    public ResponseEntity<?> getDurationByMinutes(
            @Parameter(description = "Duration in minutes (e.g., 15, 30, 45)")
            @PathVariable Integer minutes
    ) {
        try {
            Optional<ConsultationDurationDTO> duration = durationService.getDurationByMinutes(minutes);
            return duration.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", e.getMessage()));
        }
    }

    // ============================================================================
    // ✏️ ENDPOINT - MODIFICATION
    // ============================================================================
    @Operation(summary = "Update consultation duration")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDuration(
            @Parameter(description = "Duration ID to update")
            @PathVariable Long id,

            @Parameter(description = "New duration in minutes")
            @RequestParam(required = false) Integer minutes,

            @Parameter(description = "New display name")
            @RequestParam(required = false) String displayName) {

        try {
            ConsultationDurationDTO durationDTO = new ConsultationDurationDTO();
            if (minutes != null) durationDTO.setMinutes(minutes);
            if (displayName != null) durationDTO.setDisplayName(displayName);

            ConsultationDurationDTO updated = durationService.updateDuration(id, durationDTO);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("VALIDATION_ERROR", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR",
                            "Error while updating duration: " + e.getMessage()));
        }
    }

    // ============================================================================
    // 🔄 ENDPOINT - ACTIVATION/DÉSACTIVATION
    // ============================================================================
    @Operation(summary = "Activate/Deactivate a consultation duration",
            description = "Temporarily disable a duration without deleting it")
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<?> toggleDurationStatus(
            @Parameter(description = "Duration ID")
            @PathVariable Long id
    ) {
        try {
            ConsultationDurationDTO updated = durationService.toggleDurationStatus(id);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", e.getMessage()));
        }
    }

    // ============================================================================
    // 🗑️ ENDPOINT - SUPPRESSION
    // ============================================================================
    @Operation(summary = "Delete consultation duration")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDuration(
            @Parameter(description = "Duration ID to delete")
            @PathVariable Long id
    ) {
        try {
            durationService.deleteDuration(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR",
                            "Error while deleting duration: " + e.getMessage()));
        }
    }

    // ============================================================================
    // 🎯 CLASSE D'ERREUR (réutilisée)
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
