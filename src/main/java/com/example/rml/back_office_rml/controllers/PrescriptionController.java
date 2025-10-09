
package com.example.rml.back_office_rml.controllers;

import com.example.rml.back_office_rml.dto.PrescriptionDTO;
import com.example.rml.back_office_rml.dto.PrescriptionItemDTO;
import com.example.rml.back_office_rml.enums.PrescriptionStatus;
import com.example.rml.back_office_rml.services.PrescriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
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
    // 📝 CRÉER UNE ORDONNANCE
    // ============================================================================

    @Operation(summary = "Create a prescription",
            description = "Create a new prescription with multiple medications")
    @PostMapping
    public ResponseEntity<?> createPrescription(
            @RequestParam Long recordId,
            @RequestParam Long doctorId,
            @RequestParam(required = false) Long appointmentId,
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate prescriptionDate,
            @RequestParam(required = false) String instructions,
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate validUntil,
            @RequestBody List<PrescriptionItemDTO> items) {

        try {
            PrescriptionDTO dto = new PrescriptionDTO();
            dto.setRecordId(recordId);
            dto.setDoctorId(doctorId);
            dto.setAppointmentId(appointmentId);
            dto.setPrescriptionDate(prescriptionDate);
            dto.setInstructions(instructions);
            dto.setValidUntil(validUntil);
            dto.setItems(items); // ici on récupère directement la liste

            PrescriptionDTO created = prescriptionService.createPrescription(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("VALIDATION_ERROR", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("STATE_ERROR", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "Erreur interne: " + e.getMessage()));
        }
    }


    // ============================================================================
    // ✏️ MODIFIER UNE ORDONNANCE
    // ============================================================================


    @Operation(summary = "Update a prescription",
            description = "Update an existing prescription")
    @PutMapping("/{prescriptionId}")
    public ResponseEntity<?> updatePrescription(
            @PathVariable Long prescriptionId,

            @Parameter(description = "New prescription date (format: dd-MM-yyyy)")
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate prescriptionDate,

            @Parameter(description = "New general instructions")
            @RequestParam(required = false) String instructions,

            @Parameter(description = "New status")
            @RequestParam(required = false) PrescriptionStatus status,

            @Parameter(description = "New valid until date (format: dd-MM-yyyy)")
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate validUntil,

            // MÉDICAMENTS (optionnels pour modification)
            @RequestBody(required = false) List<PrescriptionItemDTO> items) { // ici on passe un DTO directement

        try {
            // CONSTRUCTION du DTO
            PrescriptionDTO dto = new PrescriptionDTO();
            dto.setPrescriptionDate(prescriptionDate);
            dto.setInstructions(instructions);
            dto.setStatus(status);
            dto.setValidUntil(validUntil);
            dto.setItems(items);


            // MISE À JOUR
            PrescriptionDTO updated = prescriptionService.updatePrescription(prescriptionId, dto);

            return ResponseEntity.ok(updated);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("NOT_FOUND", e.getMessage()));

        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("STATE_ERROR", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR",
                            "Erreur interne: " + e.getMessage()));
        }
    }
    // ============================================================================
    // 📄 RÉCUPÉRER LES ORDONNANCES
   // ============================================================================

    @Operation(summary = "Get all prescriptions",
            description = "Retrieve a list of all prescriptions")
    @GetMapping
    public ResponseEntity<List<PrescriptionDTO>> getAllPrescriptions() {
        try {
            List<PrescriptionDTO> prescriptions = prescriptionService.getAllPrescriptions();
            return ResponseEntity.ok(prescriptions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @Operation(summary = "Get prescriptions by medical record",
            description = "Retrieve all prescriptions associated with a given medical record ID")
    @GetMapping("/record/{recordId}")
    public ResponseEntity<?> getPrescriptionsByRecordId(
            @PathVariable Long recordId) {
        try {
            List<PrescriptionDTO> prescriptions = prescriptionService.getPrescriptionsByRecordId(recordId);
            return ResponseEntity.ok(prescriptions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("NOT_FOUND", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", e.getMessage()));
        }
    }


}