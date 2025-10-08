package com.example.rml.back_office_rml.controllers;

import com.example.rml.back_office_rml.dto.FamilyHistoryDTO;
import com.example.rml.back_office_rml.enums.FamilyRelation;
import com.example.rml.back_office_rml.services.FamilyHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/family-histories")
public class FamilyHistoryController {

    private final FamilyHistoryService familyHistoryService;

    public FamilyHistoryController(FamilyHistoryService familyHistoryService) {
        this.familyHistoryService = familyHistoryService;
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
    // 🆕 AJOUTER UN ANTÉCÉDENT FAMILIAL
    // ============================================================================
    @Operation(summary = "Add family history",
            description = "Adds a family history to the medical record")
    @PostMapping
    public ResponseEntity<?> addFamilyHistory(
            @Parameter(description = "Medical record ID", required = true)
            @RequestParam Long recordId,

            @Parameter(description = "Family relation (FATHER, MOTHER, BROTHER, SISTER, etc.)", required = true)
            @RequestParam FamilyRelation relation,

            @Parameter(description = "Age of the family member")
            @RequestParam(required = false) Integer age,

            @Parameter(description = "Medical condition (e.g., Diabetes, Hypertension)", required = true)
            @RequestParam String condition,

            @Parameter(description = "Additional notes")
            @RequestParam(required = false) String notes) {
        try {
            FamilyHistoryDTO dto = new FamilyHistoryDTO();
            dto.setRecordId(recordId);
            dto.setRelation(relation);
            dto.setAge(age);
            dto.setCondition(condition);
            dto.setNotes(notes);

            FamilyHistoryDTO created = familyHistoryService.addFamilyHistory(dto);
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
    // ✏️ MODIFIER UN ANTÉCÉDENT FAMILIAL
    // ============================================================================
    @Operation(summary = "Update family history",
            description = "Updates an existing family history")
    @PutMapping("/{familyHistoryId}")
    public ResponseEntity<?> updateFamilyHistory(
            @PathVariable Long familyHistoryId,

            @Parameter(description = "New family relation")
            @RequestParam(required = false) FamilyRelation relation,

            @Parameter(description = "New age")
            @RequestParam(required = false) Integer age,

            @Parameter(description = "New medical condition")
            @RequestParam(required = false) String condition,

            @Parameter(description = "New notes")
            @RequestParam(required = false) String notes) {
        try {
            FamilyHistoryDTO dto = new FamilyHistoryDTO();
            dto.setRelation(relation);
            dto.setAge(age);
            dto.setCondition(condition);
            dto.setNotes(notes);

            FamilyHistoryDTO updated = familyHistoryService.updateFamilyHistory(familyHistoryId, dto);
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
    // 📋 RÉCUPÉRER TOUS LES ANTÉCÉDENTS FAMILIAUX
    // ============================================================================
    @Operation(summary = "Get all family histories",
            description = "Retrieves all family histories")
    @GetMapping
    public ResponseEntity<?> getAllFamilyHistories() {
        try {
            List<FamilyHistoryDTO> histories = familyHistoryService.getAllFamilyHistories();
            return ResponseEntity.ok(histories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "Erreur: " + e.getMessage()));
        }
    }


    // ============================================================================
    // 📋 RÉCUPÉRER LES ANTÉCÉDENTS FAMILIAUX D'UN DOSSIER
    // ============================================================================
    @Operation(summary = "Get family histories by record ID",
            description = "Retrieves all family histories for a specific medical record")
    @GetMapping("/record/{recordId}")
    public ResponseEntity<?> getFamilyHistoriesByRecordId(@PathVariable Long recordId) {
        try {
            List<FamilyHistoryDTO> histories =
                    familyHistoryService.getFamilyHistoriesByRecordId(recordId);
            return ResponseEntity.ok(histories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "Erreur: " + e.getMessage()));
        }
    }

}