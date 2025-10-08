package com.example.rml.back_office_rml.controllers;

import com.example.rml.back_office_rml.dto.MedicalRecordDTO;
import com.example.rml.back_office_rml.services.MedicalRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
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
    // 🆕 CRÉER UN DOSSIER MÉDICAL
    // ============================================================================
    @Operation(summary = "Create a medical record")
    @PostMapping
    public ResponseEntity<?> createMedicalRecord(
            @Parameter(description = "Patient ID", required = true)
            @RequestParam Long patientId,

            @Parameter(description = "Blood type (e.g., A+, O-)")
            @RequestParam(required = false) String bloodType,

            @Parameter(description = "Known allergies")
            @RequestParam(required = false) String allergies,

            @Parameter(description = "Chronic diseases ( diabetes, hypertension)")
            @RequestParam(required = false) String chronicDiseases,

            @Parameter(description = "Current medications")
            @RequestParam(required = false) String currentMedications)
    {
        try {
            // Conversion des paramètres en DTO
            MedicalRecordDTO dto = new MedicalRecordDTO();
            dto.setPatientId(patientId);
            dto.setBloodType(bloodType);
            dto.setAllergies(allergies);
            dto.setChronicDiseases(chronicDiseases);
            dto.setCurrentMedications(currentMedications);

            MedicalRecordDTO created = medicalRecordService.createMedicalRecord(dto);
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
    // ✏️ MODIFIER UN DOSSIER MÉDICAL
    // ============================================================================
    @Operation(summary = "Update a medical record")
    @PutMapping("/{recordId}")
    public ResponseEntity<?> updateMedicalRecord(
            @PathVariable Long recordId,

            @Parameter(description = "Blood type")
            @RequestParam(required = false) String bloodType,

            @Parameter(description = "Allergies")
            @RequestParam(required = false) String allergies,

            @Parameter(description = "Chronic diseases")
            @RequestParam(required = false) String chronicDiseases,

            @Parameter(description = "Current medications")
            @RequestParam(required = false) String currentMedications)
    {
        try {
            // Conversion des paramètres en DTO
            MedicalRecordDTO dto = new MedicalRecordDTO();
            dto.setBloodType(bloodType);
            dto.setAllergies(allergies);
            dto.setChronicDiseases(chronicDiseases);
            dto.setCurrentMedications(currentMedications);

            MedicalRecordDTO updated = medicalRecordService.updateMedicalRecord(recordId, dto);
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
    // 📋 RÉCUPÉRER TOUS LES DOSSIERS MÉDICAUX
    // ============================================================================
    @Operation(summary = "Get all medical records")
    @GetMapping
    public ResponseEntity<?> getAllMedicalRecords() {
        try {
            List<MedicalRecordDTO> records = medicalRecordService.getAllMedicalRecords();
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "Erreur: " + e.getMessage()));
        }
    }

    // ============================================================================
    // 📋 RÉCUPÉRER LE DOSSIER D'UN PATIENT
    // ============================================================================
    @Operation(summary = "Get medical record by patient ID")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getMedicalRecordByPatientId(@PathVariable Long patientId) {
        try {
            MedicalRecordDTO record = medicalRecordService.getMedicalRecordByPatientId(patientId);
            return ResponseEntity.ok(record);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("NOT_FOUND", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "Erreur: " + e.getMessage()));
        }
    }

    // ============================================================================
    // 📋 RÉCUPÉRER UN DOSSIER PAR SON Numéro
    // ============================================================================
    @Operation(summary = "Get medical record by Medical record number ")
    @GetMapping("/{recordNumber}")
    public ResponseEntity<?> getMedicalRecordById(@PathVariable String recordNumber) {
        try {
            MedicalRecordDTO record = medicalRecordService.getMedicalRecordByRecordNumber(recordNumber);
            return ResponseEntity.ok(record);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("NOT_FOUND", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "Erreur: " + e.getMessage()));
        }
    }
}