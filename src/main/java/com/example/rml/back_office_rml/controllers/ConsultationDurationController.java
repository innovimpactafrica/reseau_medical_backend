package com.example.rml.back_office_rml.controllers;

import com.example.rml.back_office_rml.dto.ConsultationDurationDTO;
import com.example.rml.back_office_rml.services.ConsultationDurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
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

    @Operation(summary = "Créer une nouvelle durée de consultation",
            description = "Ajoute une nouvelle durée disponible pour les consultations")
    @PostMapping
    public ResponseEntity<?> createDuration(@Valid @RequestBody ConsultationDurationDTO durationDTO) {
        try {
            ConsultationDurationDTO created = durationService.createDuration(durationDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("VALIDATION_ERROR", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR",
                            "Erreur lors de la création : " + e.getMessage()));
        }
    }

    // ============================================================================
    // 📋 ENDPOINTS DE LECTURE
    // ============================================================================

    @Operation(summary = "Obtenir toutes les durées de consultation",
            description = "Retourne la liste complète des durées disponibles")
    @GetMapping
    public ResponseEntity<List<ConsultationDurationDTO>> getAllDurations() {
        try {
            List<ConsultationDurationDTO> durations = durationService.getAllDurations();
            return ResponseEntity.ok(durations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Obtenir les durées actives",
            description = "Retourne seulement les durées activées pour l'utilisation")
    @GetMapping("/active")
    public ResponseEntity<List<ConsultationDurationDTO>> getActiveDurations() {
        try {
            List<ConsultationDurationDTO> durations = durationService.getActiveDurations();
            return ResponseEntity.ok(durations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Obtenir une durée par son ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getDurationById(
            @Parameter(description = "ID de la durée")
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

    @Operation(summary = "Obtenir une durée par le nombre de minutes")
    @GetMapping("/by-minutes/{minutes}")
    public ResponseEntity<?> getDurationByMinutes(
            @Parameter(description = "Nombre de minutes (ex: 15, 30, 45)")
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

    @Operation(summary = "Modifier une durée de consultation")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDuration(
            @Parameter(description = "ID de la durée à modifier")
            @PathVariable Long id,
            @Valid @RequestBody ConsultationDurationDTO durationDTO
    ) {
        try {
            ConsultationDurationDTO updated = durationService.updateDuration(id, durationDTO);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("VALIDATION_ERROR", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR",
                            "Erreur lors de la modification : " + e.getMessage()));
        }
    }

    // ============================================================================
    // 🔄 ENDPOINT - ACTIVATION/DÉSACTIVATION
    // ============================================================================

    @Operation(summary = "Activer/désactiver une durée",
            description = "Permet de désactiver temporairement une durée sans la supprimer")
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<?> toggleDurationStatus(
            @Parameter(description = "ID de la durée")
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

    @Operation(summary = "Supprimer une durée de consultation")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDuration(
            @Parameter(description = "ID de la durée à supprimer")
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
                            "Erreur lors de la suppression : " + e.getMessage()));
        }
    }

    // ============================================================================
    // 🎯 CLASSE D'ERREUR (réutilisée)
    // ============================================================================

    public static class ErrorResponse {
        private final String error;
        private final String message;
        private final long timestamp;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }

        public String getError() { return error; }
        public String getMessage() { return message; }
        public long getTimestamp() { return timestamp; }
    }
}