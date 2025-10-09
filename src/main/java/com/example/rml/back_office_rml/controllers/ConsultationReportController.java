package com.example.rml.back_office_rml.controllers;

import com.example.rml.back_office_rml.dto.ConsultationReportDTO;
import com.example.rml.back_office_rml.enums.ReportType;
import com.example.rml.back_office_rml.services.ConsultationReportService;
import com.example.rml.back_office_rml.util.FileTransferUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/consultation-reports")
public class ConsultationReportController {

    private final ConsultationReportService reportService;

    public ConsultationReportController(ConsultationReportService reportService) {
        this.reportService = reportService;
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
    // 📝 CRÉER UN COMPTE RENDU TEXTE
    // ============================================================================
    @Operation(summary = "Create text consultation report")
    @PostMapping("/text")
    public ResponseEntity<?> createTextReport(

            @Parameter(description = "Medical record ID", required = true)
            @RequestParam Long recordId,

            @Parameter(description = "Doctor ID", required = true)
            @RequestParam Long doctorId,

            @Parameter(description = "Appointment ID (optional)")
            @RequestParam(required = false) Long appointmentId,

            @Parameter(description = "Report date (format: dd-MM-yyyy)", required = true)
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate reportDate,

            @Parameter(description = "Report title", required = true)
            @RequestParam String title,

            @Parameter(description = "Report category , ex: Radiologie" ,required = true)
            @RequestParam String category,

            @Parameter(description = "Report content", required = true)
            @RequestParam String content) {


        try {
            ConsultationReportDTO dto = new ConsultationReportDTO();
            dto.setRecordId(recordId);
            dto.setDoctorId(doctorId);
            dto.setAppointmentId(appointmentId);
            dto.setReportDate(reportDate);
            dto.setType(ReportType.TEXT);
            dto.setTitle(title);
            dto.setCategory(category);
            dto.setContent(content);
            dto.setIsModifiable(true);

            ConsultationReportDTO created = reportService.createReport(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("VALIDATION_ERROR", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", e.getMessage()));
        }
    }

    // ============================================================================
    // 🎤 CRÉER UN COMPTE RENDU AUDIO
    // ============================================================================
    @Operation(summary = "Create audio consultation report")
    @PostMapping(value = "/audio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createAudioReport(
            @RequestParam Long recordId,
            @RequestParam Long doctorId,
            @RequestParam(required = false) Long appointmentId,
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate reportDate,
            @RequestParam String title,
            @RequestParam String category,
            @RequestParam("audioFile") MultipartFile audioFile) {

        try {
            // VALIDATION du fichier audio
            if (audioFile == null || audioFile.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("VALIDATION_ERROR",
                                "Le fichier audio est obligatoire"));
            }

            String contentType = audioFile.getContentType();
            if (contentType == null || !contentType.startsWith("audio/")) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("VALIDATION_ERROR",
                                "Le fichier doit être un audio"));
            }

            // ✅ UPLOAD du fichier audio
            String audioUrl = FileTransferUtil.handleFileUpload(audioFile);

            ConsultationReportDTO dto = new ConsultationReportDTO();
            dto.setRecordId(recordId);
            dto.setDoctorId(doctorId);
            dto.setAppointmentId(appointmentId);
            dto.setReportDate(reportDate);
            dto.setType(ReportType.AUDIO);
            dto.setTitle(title);
            dto.setCategory(category);
            dto.setFilePath(audioUrl);
            dto.setIsModifiable(false);

            ConsultationReportDTO created = reportService.createReport(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("VALIDATION_ERROR", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", e.getMessage()));
        }
    }

    // ============================================================================
    // 🎥 CRÉER UN COMPTE RENDU VIDÉO
    // ============================================================================
    @Operation(summary = "Create video consultation report")
    @PostMapping(value = "/video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createVideoReport(
            @RequestParam Long recordId,
            @RequestParam Long doctorId,
            @RequestParam(required = false) Long appointmentId,
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate reportDate,
            @RequestParam String title,
            @RequestParam String category,
            @RequestParam("videoFile") MultipartFile videoFile) {

        try {
            // VALIDATION du fichier vidéo
            if (videoFile == null || videoFile.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("VALIDATION_ERROR",
                                "Le fichier vidéo est obligatoire"));
            }

            String contentType = videoFile.getContentType();
            if (contentType == null || !contentType.startsWith("video/")) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("VALIDATION_ERROR",
                                "Le fichier doit être une vidéo"));
            }

            // ✅ UPLOAD du fichier vidéo
            String videoUrl = FileTransferUtil.handleFileUpload(videoFile);

            ConsultationReportDTO dto = new ConsultationReportDTO();
            dto.setRecordId(recordId);
            dto.setDoctorId(doctorId);
            dto.setAppointmentId(appointmentId);
            dto.setReportDate(reportDate);
            dto.setType(ReportType.VIDEO);
            dto.setTitle(title);
            dto.setCategory(category);
            dto.setFilePath(videoUrl);
            dto.setIsModifiable(false);

            ConsultationReportDTO created = reportService.createReport(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("VALIDATION_ERROR", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", e.getMessage()));
        }
    }

    // ============================================================================
    // ✏️ MODIFIER UN COMPTE RENDU
    // ============================================================================
    @Operation(summary = "Update consultation report type text")
    @PutMapping("/{reportId}")
    public ResponseEntity<?> updateReport(
            @PathVariable Long reportId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate reportDate,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String content) {

        try {
            ConsultationReportDTO dto = new ConsultationReportDTO();
            dto.setReportDate(reportDate);
            dto.setTitle(title);
            dto.setCategory(category);
            dto.setContent(content);

            ConsultationReportDTO updated = reportService.updateReport(reportId, dto);
            return ResponseEntity.ok(updated);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("NOT_FOUND", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("STATE_ERROR", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", e.getMessage()));
        }
    }

    // ============================================================================
    // 📋 RÉCUPÉRER TOUS LES COMPTES RENDUS
    // ============================================================================
    @Operation(summary = "Get all consultation reports")
    @GetMapping
    public ResponseEntity<?> getAllReports() {
        try {
            List<ConsultationReportDTO> reports = reportService.getAllReports();
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", e.getMessage()));
        }
    }

    // ============================================================================
    // 📋 RÉCUPÉRER LES COMPTES RENDUS D'UN DOSSIER
    // ============================================================================
    @Operation(summary = "Get reports by medical record")
    @GetMapping("/record/{recordId}")
    public ResponseEntity<?> getReportsByRecordId(@PathVariable Long recordId) {
        try {
            List<ConsultationReportDTO> reports =
                    reportService.getReportsByRecordId(recordId);
            return ResponseEntity.ok(reports);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("NOT_FOUND", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", e.getMessage()));
        }
    }



}