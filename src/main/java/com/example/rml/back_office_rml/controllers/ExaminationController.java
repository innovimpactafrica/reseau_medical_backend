package com.example.rml.back_office_rml.controllers;

import com.example.rml.back_office_rml.dto.ExaminationDTO;
import com.example.rml.back_office_rml.enums.ExaminationType;
import com.example.rml.back_office_rml.services.ExaminationService;
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
@RequestMapping("/api/examinations")
public class ExaminationController {

    private final ExaminationService examinationService;

    public ExaminationController(ExaminationService examinationService) {
        this.examinationService = examinationService;
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
    // 📋 CREATE EXAMINATION WITH MULTIPLE FILES (PDF, Images)
    // ============================================================================
    /**
     * Crée un examen médical avec plusieurs fichiers de résultats.
     *
     * Cas d'usage :
     * - Upload de résultats d'analyses (PDF)
     * - Upload d'images radiographiques
     * - Upload d'images d'échographie
     *
     * Étapes :
     * 1. Recevoir les données de l'examen et les fichiers
     * 2. Uploader tous les fichiers via FileTransferUtil.uploadPictures()
     * 3. Récupérer la liste des URLs
     * 4. Créer l'entité Examination avec ces URLs
     * 5. Sauvegarder en base de données
     */

    @Operation(summary = "Create examination with multiple result files",
            description = "Create a medical examination and upload multiple result files (PDF, images)")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createExamination(
            @Parameter(description = "Medical record ID", required = true)
            @RequestParam Long recordId,

            @Parameter(description = "Prescribing doctor ID", required = true)
            @RequestParam Long doctorId,

            @Parameter(description = "Examination date (format: dd-MM-yyyy)", required = true)
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate examinationDate,

            @Parameter(description = "Examination type (BLOOD_TEST, XRAY, MRI, etc.)", required = true)
            @RequestParam ExaminationType type,

            @Parameter(description = "Examination title", required = true)
            @RequestParam String title,

            @Parameter(description = "Examination results")
            @RequestParam(required = false) String results,

            @Parameter(description = "Doctor's notes")
            @RequestParam(required = false) String notes,

            @Parameter(description = "List of files to upload (PDF, image)")
            @RequestParam(value = "files", required = false) List<MultipartFile> files) {

        try {
            ExaminationDTO dto = new ExaminationDTO();
            dto.setRecordId(recordId);
            dto.setDoctorId(doctorId);
            dto.setExaminationDate(examinationDate);
            dto.setType(type);
            dto.setTitle(title);
            dto.setResults(results);
            dto.setNotes(notes);

            // Si l'utilisateur a fourni des fichiers (liste non vide),
            // on vérifie ensuite que chaque fichier n'est pas vide avant de les uploader
            if (files != null && !files.isEmpty()) {
                for (MultipartFile file : files) {
                    if (file.isEmpty()) {
                        return ResponseEntity.badRequest()
                                .body(new ErrorResponse("VALIDATION_ERROR", "One of the files is empty"));
                    }
                }
                List<String> fileUrls = FileTransferUtil.uploadPictures(files);
                dto.setResultFiles(fileUrls);
            }


            ExaminationDTO created = examinationService.addExamination(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("NOT_FOUND", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "Internal error: " + e.getMessage()));
        }
    }


    // ============================================================================
    // ✏️ MODIFIER UN EXAMEN
    // ============================================================================
    @Operation(summary = "Update examination",
            description = "Updates an existing medical examination")
    @PutMapping("/{examinationId}")
    public ResponseEntity<?> updateExamination(
            @PathVariable Long examinationId,

            @Parameter(description = "New examination date (format: dd-MM-yyyy)")
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate examinationDate,

            @Parameter(description = "New examination type")
            @RequestParam(required = false) ExaminationType type,

            @Parameter(description = "New title")
            @RequestParam(required = false) String title,

            @Parameter(description = "New results")
            @RequestParam(required = false) String results,

            @Parameter(description = "New notes")
            @RequestParam(required = false) String notes,

            @Parameter(description = "List of files to upload (PDF, image)")
            @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        try {
            ExaminationDTO dto = new ExaminationDTO();
            dto.setExaminationDate(examinationDate);
            dto.setType(type);
            dto.setTitle(title);
            dto.setResults(results);
            dto.setNotes(notes);

            // Si l'utilisateur a fourni des fichiers (liste non vide),
            // on vérifie ensuite que chaque fichier n'est pas vide avant de les uploader
            if (files != null && !files.isEmpty()) {
                for (MultipartFile file : files) {
                    if (file.isEmpty()) {
                        return ResponseEntity.badRequest()
                                .body(new ErrorResponse("VALIDATION_ERROR", "One of the files is empty"));
                    }
                }
                List<String> fileUrls = FileTransferUtil.uploadPictures(files);
                dto.setResultFiles(fileUrls);
            }

            ExaminationDTO updated = examinationService.updateExamination(examinationId, dto);
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
    // ✏️ LISTER LES EXAMENS
    // ============================================================================
    @Operation(summary = "Get all examinations",
            description = "Retrieves all medical examinations")
    @GetMapping
    public ResponseEntity<?> getAllExaminations() {
        try {
            List<ExaminationDTO> examinations = examinationService.getAllExaminations();
            return ResponseEntity.ok(examinations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "Erreur: " + e.getMessage()));
        }
    }

    // ============================================================================
    // ✏️AFFICHER UN EXAMEN PAR SON ID
    // ============================================================================
    @Operation(summary = "Get examinations by record ID",
            description = "Retrieves all examinations for a specific medical record")
    @GetMapping("/record/{recordId}")
    public ResponseEntity<?> getExaminationsByRecordId(@PathVariable Long recordId) {
        try {
            List<ExaminationDTO> examinations =
                    examinationService.getExaminationsByRecordId(recordId);
            return ResponseEntity.ok(examinations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "Erreur: " + e.getMessage()));
        }
    }

}