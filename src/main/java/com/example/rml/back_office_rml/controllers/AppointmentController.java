package com.example.rml.back_office_rml.controllers;

import com.example.rml.back_office_rml.dto.AppointmentRequestDTO;
import com.example.rml.back_office_rml.dto.AppointmentResponseDTO;
import com.example.rml.back_office_rml.enums.AppointmentStatus;
import com.example.rml.back_office_rml.services.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
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
    // 📅 RÉSERVER UN RENDEZ-VOUS
    // ============================================================================
    @Operation(summary = "Book a new appointment",
            description = "Patient books an available slot with a doctor")
    @PostMapping("/book")
    public ResponseEntity<?> bookAppointment(
            @Parameter(description = "Patient ID", required = true)
            @RequestParam Long patientId,

            @Parameter(description = "Slot ID", required = true)
            @RequestParam Long slotId,

            @Parameter(description = "Consultation reason", required = false)
            @RequestParam String consultationReason) {
        try {
            AppointmentRequestDTO request = new AppointmentRequestDTO();
            request.setPatientId(patientId);
            request.setSlotId(slotId);
            request.setConsultationReason(consultationReason);

            AppointmentResponseDTO appointment = appointmentService.bookAppointment(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(appointment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("VALIDATION_ERROR", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("CONFLICT_ERROR", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "Erreur: " + e.getMessage()));
        }
    }

    // ============================================================================
    // 📋 RÉCUPÉRER TOUS LES RENDEZ-VOUS
    // ============================================================================
    @Operation(summary = "Get all appointments", description = "Retrieve all appointments in the system")
    @GetMapping
    public ResponseEntity<?> getAllAppointments() {
        try {
            List<AppointmentResponseDTO> appointments = appointmentService.getAllAppointments();
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "Erreur: " + e.getMessage()));
        }
    }

    // ============================================================================
    // 📋 RÉCUPÉRER LES RENDEZ-VOUS D'UN PATIENT PAR STATUT
    // ============================================================================
    @Operation(summary = "Get patient appointments by status",
            description = "Retrieve all appointments for a specific patient filtered by status")
    @GetMapping("/patient/{patientId}/status/{status}")
    public ResponseEntity<?> getPatientAppointmentsByStatus(
            @PathVariable Long patientId,
            @PathVariable AppointmentStatus status) {
        try {
            List<AppointmentResponseDTO> appointments =
                    appointmentService.getPatientAppointmentsByStatus(patientId, status);
            return ResponseEntity.ok(appointments);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("NOT_FOUND", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "Erreur: " + e.getMessage()));
        }
    }

    // ============================================================================
    // 📋 RÉCUPÉRER LES RENDEZ-VOUS D'UN MÉDECIN PAR STATUT
    // ============================================================================
    @Operation(summary = "Get doctor appointments by status",
            description = "Retrieve all appointments for a specific doctor filtered by status")
    @GetMapping("/doctor/{doctorId}/status/{status}")
    public ResponseEntity<?> getDoctorAppointmentsByStatus(
            @PathVariable Long doctorId,
            @PathVariable AppointmentStatus status) {
        try {
            List<AppointmentResponseDTO> appointments =
                    appointmentService.getDoctorAppointmentsByStatus(doctorId, status);
            return ResponseEntity.ok(appointments);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("NOT_FOUND", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "Erreur: " + e.getMessage()));
        }
    }

    // ============================================================================
    // ✅ CONFIRMER UN RENDEZ-VOUS
    // ============================================================================
    @Operation(summary = "Confirm an appointment",
            description = "Confirm a pending appointment by appointment ID")
    @PatchMapping("/{appointmentId}/confirm")
    public ResponseEntity<?> confirmAppointment(@PathVariable Long appointmentId) {
        try {
            AppointmentResponseDTO confirmed = appointmentService.confirmAppointment(appointmentId);
            return ResponseEntity.ok(confirmed);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("NOT_FOUND", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("STATE_ERROR", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "Erreur: " + e.getMessage()));
        }
    }

    // ============================================================================
    // ✅ TERMINER UN RENDEZ-VOUS
    // ============================================================================
    @Operation(summary = "Terminate an appointment",
            description = "Mark a confirmed appointment as completed")
    @PatchMapping("/{appointmentId}/terminate")
    public ResponseEntity<?> terminateAppointment(@PathVariable Long appointmentId) {
        try {
            AppointmentResponseDTO completed = appointmentService.terminateAppointment(appointmentId);
            return ResponseEntity.ok(completed);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("NOT_FOUND", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("STATE_ERROR", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "Erreur: " + e.getMessage()));
        }
    }

    // ============================================================================
    // ❌ ANNULER UN RENDEZ-VOUS
    // ============================================================================
    @Operation(summary = "Cancel an appointment",
            description = "Cancel an appointment by ID")
    @PatchMapping("/{appointmentId}/cancel")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long appointmentId) {
        try {
            appointmentService.cancelAppointment(appointmentId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("NOT_FOUND", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("STATE_ERROR", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "Erreur: " + e.getMessage()));
        }
    }

    // ============================================================================
    // ✏️ MODIFIER UN RENDEZ-VOUS
    // ============================================================================
    @Operation(summary = "Update an appointment",
            description = "Update an existing appointment (slot or consultation reason)")
    @PutMapping("/{appointmentId}")
    public ResponseEntity<?> updateAppointment(
            @PathVariable Long appointmentId,
            @Parameter(description = "New slot ID (optional)") @RequestParam(required = false) Long slotId,
            @Parameter(description = "New consultation reason (optional)") @RequestParam(required = false) String consultationReason) {
        try {
            AppointmentRequestDTO request = new AppointmentRequestDTO();
            request.setSlotId(slotId);
            request.setConsultationReason(consultationReason);

            AppointmentResponseDTO updated = appointmentService.updateAppointment(appointmentId, request);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("NOT_FOUND", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("STATE_ERROR", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "Erreur: " + e.getMessage()));
        }
    }

    // ============================================================================
    // 🗑️ SUPPRIMER UN RENDEZ-VOUS
    // ============================================================================
    @Operation(summary = "Delete an appointment", description = "Delete an appointment from the system")
    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<?> deleteAppointment(@PathVariable Long appointmentId) {
        try {
            appointmentService.deleteAppointment(appointmentId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("NOT_FOUND", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("STATE_ERROR", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "Erreur: " + e.getMessage()));
        }
    }
}
