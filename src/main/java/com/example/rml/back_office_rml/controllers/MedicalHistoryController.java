package com.example.rml.back_office_rml.controllers;

import com.example.rml.back_office_rml.dto.MedicalHistoryDTO;
import com.example.rml.back_office_rml.services.MedicalHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/medical-histories")
public class MedicalHistoryController {

    private final MedicalHistoryService medicalHistoryService;

    public MedicalHistoryController(MedicalHistoryService medicalHistoryService) {
        this.medicalHistoryService = medicalHistoryService;
    }

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
    // 🆕 AJOUTER UN ANTÉCÉDENT MÉDICAL
    // ============================================================================
    @Operation(summary = "Add medical history")
    @PostMapping
    public ResponseEntity<?> addMedicalHistory(
            @Parameter(description = "Medical record ID", required = true)
            @RequestParam Long recordId,

            @Parameter(description = "Diagnosis (e.g., Flu, Fracture)", required = true)
            @RequestParam String diagnosis,

            @Parameter(description = "Detailed description")
            @RequestParam(required = false) String description) {
        try {
            MedicalHistoryDTO dto = new MedicalHistoryDTO();
            dto.setRecordId(recordId);
            dto.setDiagnosis(diagnosis);
            dto.setDescription(description);

            MedicalHistoryDTO created = medicalHistoryService.addMedicalHistory(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("VALIDATION_ERROR", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "Erreur: " + e.getMessage()));
        }
    }
    // ============================================================================
    // ✏️ MODIFIER UN ANTÉCÉDENT MÉDICAL
    // ============================================================================
    @Operation(summary = "Update medical history",
            description = "Update an existing medical history")
    @PutMapping("/{historyId}")
    public ResponseEntity<?> updateMedicalHistory(
            @PathVariable Long historyId,

            @Parameter(description = "New diagnosis")
            @RequestParam(required = false) String diagnosis,

            @Parameter(description = "New description")
            @RequestParam(required = false) String description) {
        try {
            MedicalHistoryDTO dto = new MedicalHistoryDTO();
            dto.setDiagnosis(diagnosis);
            dto.setDescription(description);

            MedicalHistoryDTO updated = medicalHistoryService.updateMedicalHistory(historyId, dto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("NOT_FOUND", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "Erreur: " + e.getMessage()));
        }
    }

    // ============================================================================
    // 📋 RÉCUPÉRER TOUS LES ANTÉCÉDENTS
    // ============================================================================
    @Operation(summary = "Get all medical histories")
    @GetMapping
    public ResponseEntity<?> getAllMedicalHistories() {
        try {
            List<MedicalHistoryDTO> histories = medicalHistoryService.getAllMedicalHistories();
            return ResponseEntity.ok(histories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "Erreur: " + e.getMessage()));
        }
    }

    // ============================================================================
    // 📋 RÉCUPÉRER LES ANTÉCÉDENTS D'UN DOSSIER
    // ============================================================================
    @Operation(summary = "Get medical histories by record ID")
    @GetMapping("/record/{recordId}")
    public ResponseEntity<?> getMedicalHistoriesByRecordId(@PathVariable Long recordId) {
        try {
            List<MedicalHistoryDTO> histories =
                    medicalHistoryService.getMedicalHistoriesByRecordId(recordId);
            return ResponseEntity.ok(histories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "Erreur: " + e.getMessage()));
        }
    }
}