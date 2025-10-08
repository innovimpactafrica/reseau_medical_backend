// RequestContainerController.java
package com.example.rml.back_office_rml.controllers;

import com.example.rml.back_office_rml.dto.*;
import com.example.rml.back_office_rml.enums.MedicalSpecialty;
import com.example.rml.back_office_rml.enums.UserRole;
import com.example.rml.back_office_rml.enums.UserStatus;
import com.example.rml.back_office_rml.services.*;
import com.example.rml.back_office_rml.util.FileTransferUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class RequestContainerController {


    private final RequestContainerService requestContainerService;
    private final RegisterDoctorService registerDoctorService;
    private final RegisterHealthCenterService registerHealthCenterService;
    private final DoctorDocumentService doctorDocumentService;
    private final HealthCenterDocumentService healthCenterDocumentService;

    public RequestContainerController (RequestContainerService requestContainerService , RegisterDoctorService registerDoctorService , RegisterHealthCenterService registerHealthCenterService, DoctorDocumentService doctorDocumentService , HealthCenterDocumentService healthCenterDocumentService) {
        this.requestContainerService =requestContainerService;
        this.registerDoctorService = registerDoctorService;
        this.registerHealthCenterService= registerHealthCenterService;
        this.doctorDocumentService = doctorDocumentService;
        this.healthCenterDocumentService = healthCenterDocumentService;
    }

    /**
     * Récupère toutes les demandes (médecins et centres de santé)
     */
    @Operation(summary = "Get all registration requests",
            description = "Returns the complete list of all registration requests (doctors and health centers)")
    @GetMapping("/all")
    public ResponseEntity<List<RequestContainerDto>> listAllRequests() {
        try {
            List<RequestContainerDto> requests = requestContainerService.listAllRequests();
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Récupère uniquement les demandes des médecins
     */
    @Operation(summary = "Get all doctor registration requests",
            description = "Returns the list of registration requests submitted by doctors")
    @GetMapping("/doctors")
    public ResponseEntity<List<RequestDoctorDTO>> listDoctorRequests() {
        try {
            List<RequestDoctorDTO> doctorRequests = requestContainerService.listDoctorRequests();
            return ResponseEntity.ok(doctorRequests);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Récupère uniquement les demandes des centres de santé
     */
    @Operation(summary = "Get all health center registration requests",
            description = "Returns the list of registration requests submitted by health centers")
    @GetMapping("/healthcenters")
    public ResponseEntity<List<RequestHealthCenterDTO>> listHealthCenterRequests() {
        try {
            List<RequestHealthCenterDTO> healthCenterRequests = requestContainerService.listHealthCenterRequests();
            return ResponseEntity.ok(healthCenterRequests);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Récupère les demandes par statut
     */
    @Operation(summary = "Get registration requests by status",
            description = "Returns all registration requests with a specific status (PENDING, APPROVED, REJECTED)")
    @GetMapping("/by-status")
    public ResponseEntity<List<RequestContainerDto>> listRequestsByStatus(
            @Parameter(description = "Status of requests to retrieve", required = true)
            @RequestParam UserStatus status) {
        try {
            List<RequestContainerDto> requests = requestContainerService.listRequestsByStatus(status);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Récupère les demandes par rôle et statut
     */
    @Operation(summary = "Get registration requests by role and status",
            description = "Returns registration requests filtered by role (DOCTOR/HEALTH_CENTER) and status")
    @GetMapping("/by-role-status")
    public ResponseEntity<List<RequestContainerDto>> listRequestsByRoleAndStatus(
            @Parameter(description = "Role of the users", required = true)
            @RequestParam UserRole role,
            @Parameter(description = "Status of the requests", required = true)
            @RequestParam UserStatus status) {
        try {
            List<RequestContainerDto> requests = requestContainerService.listRequestsByRoleAndStatus(role, status);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @Operation(summary = "Get doctor requests by status",
            description = "Returns the list of doctor registration requests with a specific status")
    @GetMapping("/doctors/by-status")
    public ResponseEntity<List<RequestDoctorDTO>> listDoctorRequestsByStatus(
            @Parameter(description = "Status of the requests", required = true)
            @RequestParam UserStatus status) {
        try {
            List<RequestDoctorDTO> requests = requestContainerService.listDoctorRequestsByStatus(status);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @Operation(summary = "Get health center requests by status",
            description = "Returns the list of health center registration requests with a specific status")
    @GetMapping("/health-centers/by-status")
    public ResponseEntity<List<RequestHealthCenterDTO>> listHealthCenterRequestsByStatus(
            @Parameter(description = "Status of the requests", required = true)
            @RequestParam UserStatus status) {
        try {
            List<RequestHealthCenterDTO> requests = requestContainerService.listHealthCenterRequestsByStatus(status);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/stats")
    @Operation(
            summary = "Get registration request statistics",
            description = "Returns full statistics of registration requests, including global totals " +
                    "and details per role (doctors and health centers), broken down by status " +
                    "(pending, approved, refused)"
    )
    public ResponseEntity<RequestStatsDTO> getRequestStatistics() {
        try {
            RequestStatsDTO stats = requestContainerService.getRegistrationRequestStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/approve/{userId}")
    @Operation(
            summary = "Approve a registration request",
            description = "Changes the status of a request from PENDING to APPROVED. " +
                    "Only requests currently in PENDING status can be approved."
    )
    public ResponseEntity<RequestContainerDto> approveRequest(
            @Parameter(description = "ID of the user whose request should be approved")
            @PathVariable Long userId) {
        try {
            RequestContainerDto approvedRequest = requestContainerService.approveRequest(userId);
            return ResponseEntity.ok(approvedRequest);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/refuse/{userId}")
    @Operation(
            summary = "Refuse a registration request",
            description = "Changes the status of a request from PENDING to REFUSED. " +
                    "Only requests currently in PENDING status can be refused."
    )
    public ResponseEntity<RequestContainerDto> refuseRequest(
            @Parameter(description = "ID of the user whose request should be refused")
            @PathVariable Long userId) {
        try {
            RequestContainerDto refusedRequest = requestContainerService.refuseRequest(userId);
            return ResponseEntity.ok(refusedRequest);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Register a new doctor")
    @PostMapping(value = "/doctor" ,consumes = "multipart/form-data")
    public ResponseEntity<?> registerDoctor(
            @Parameter(description = "Doctor first name", required = true)
            @RequestParam @NotBlank String firstName,

            @Parameter(description = "Doctor last name", required = true)
            @RequestParam @NotBlank String lastName,


            @Parameter(description = "User email", required = true)
            @RequestParam @NotBlank @Email String email,

            @Parameter(description = "User password", required = true)
            @RequestParam @NotBlank String password,


            @Parameter(description = "Medical specialty", required = true)
            @RequestParam MedicalSpecialty specialty,

            @Parameter(description = "Phone number", required = true)
            @RequestParam @NotBlank String phone,

            @Parameter(description = "Doctor's ID photo")
            @RequestPart(required = false) MultipartFile photo,

            @Parameter(description = "Doctor's supporting documents")
            @RequestPart(required = false) MultipartFile documents) {

        try {
            RegisterDoctorDTO dto = new RegisterDoctorDTO();
            dto.setEmail(email);
            dto.setPassword(password);
            dto.setLastName(lastName);
            dto.setFirstName(firstName);
            dto.setSpecialty(specialty);
            dto.setPhone(phone);
            RegisterDoctorDTO result = registerDoctorService.createDoctorUser(dto);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Get doctors by specialty",
            description = "Retrieve the list of doctors filtered by their medical specialty"
    )
    @GetMapping("/doctors/by-specialty")
    public ResponseEntity<?> getDoctorsBySpecialty(
            @Parameter(description = "Medical specialty used to filter doctors")
            @RequestParam MedicalSpecialty specialty) {
        try {
            List<RequestDoctorDTO> result = registerDoctorService.getDoctorsBySpecialty(specialty);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error: " + e.getMessage());
        }
    }

    @Operation(summary = "Register a new health center")
    @PostMapping(value = "/health_center",consumes = "multipart/form-data")
    public ResponseEntity<?> registerHealthCenter(
            @Parameter(description = "Health center name", required = true)
            @RequestParam @NotBlank String name,

            @Parameter(description = "Health center address", required = true)
            @RequestParam @NotBlank String address,

            @Parameter(description = "Health center opening hours", required = true)
            @RequestParam @NotBlank String openingHours,

            @Parameter(description = "Email of the health center", required = true)
            @RequestParam @NotBlank @Email String email,

            @Parameter(description = "Password for the health center account", required = true)
            @RequestParam @NotBlank String password,

            @Parameter(description = "Contact person name", required = true)
            @RequestParam @NotBlank String contactPerson,

            @Parameter(description = "Contact person phone number", required = true)
            @RequestParam @NotBlank String contactPhone,

            @Parameter(description = "Health center logo")
            @RequestPart(required = false) MultipartFile logo,

            @Parameter(description = "Health center documents")
            @RequestPart(required = false) MultipartFile documents
    ) {
        try {
            // Prepare DTO
            RegisterHealthCenterDTO dto = new RegisterHealthCenterDTO();
            dto.setHealthCenterName(name);
            dto.setHealthCenterAddress(address);
            dto.setOpeningHours(openingHours);
            dto.setEmail(email);
            dto.setPassword(password);
            dto.setReferentName(contactPerson);
            dto.setReferentPhone(contactPhone);


            // Call the service
            RegisterHealthCenterDTO result = registerHealthCenterService.registerHealthCenter(dto);

            // Return response
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * Ajoute les fichiers justificatifs et la photo d’un médecin.
     *
     * Étapes :
     * 1. L’utilisateur envoie une photo + des documents
     * 2. Les fichiers sont uploadés via FileTransferUtil
     * 3. On enregistre les URLs dans DoctorDocument
     */
    @Operation(summary = "Add doctor documents", description = "Upload photo and supporting documents for a doctor")
    @PostMapping(value = "/doctor-documents", consumes = "multipart/form-data")
    public ResponseEntity<?> addDoctorDocuments(
            @Parameter(description = "Doctor ID", required = true)
            @RequestParam Long doctorId,

            @Parameter(description = "Photo of the doctor")
            @RequestParam(required = false) MultipartFile photo,

            @Parameter(description = "Supporting documents (PDF, images, etc.)")
            @RequestParam(required = false) List<MultipartFile> documents) {

        try {
            DoctorDocumentDTO dto = new DoctorDocumentDTO();
            dto.setDoctorId(doctorId);

            // Upload de la photo si présente
            if (photo != null && !photo.isEmpty()) {
                String photoUrl = FileTransferUtil.handleFileUpload(photo);
                dto.setPhoto(photoUrl);
            }

            // Upload des documents (plusieurs fichiers possibles)
            if (documents != null && !documents.isEmpty()) {
                for (MultipartFile file : documents) {
                    if (file.isEmpty()) {
                        return ResponseEntity.badRequest()
                                .body("One of the files is empty");
                    }
                }
                List<String> documentUrls = FileTransferUtil.uploadPictures(documents);
                dto.setDocumentUrl(String.join(",", documentUrls));
            }

            // Appel du service pour sauvegarder
            DoctorDocumentDTO saved = doctorDocumentService.addDoctorDocument(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload failed: " + e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error: " + e.getMessage());
        }
    }


    //  Récupérer tous les documents d’un médecin
    @Operation(summary = "Get doctor's documents", description = "Retrieve all uploaded documents for a doctor")
    @GetMapping("/doctor-documents/{doctorId}")
    public ResponseEntity<?> getDoctorDocuments(@PathVariable Long doctorId) {
        List<DoctorDocumentDTO> documents = doctorDocumentService.getDocumentsByDoctor(doctorId);
        return ResponseEntity.ok(documents);
    }

    // Ajouter des documents pour un centre de santé
    @Operation(
            summary = "Add documents to a health center",
            description = "Upload a logo (single file) and multiple supporting documents (PDF, images) for a health center"
    )
    @PostMapping(value = "/health-center-documents", consumes = {"multipart/form-data"})
    public ResponseEntity<?> addDocuments(
            @RequestParam Long centerId,
            @RequestParam(required = false) MultipartFile logo,
            @RequestParam(required = false) List<MultipartFile> documents) {

        try {
            HealthCenterDocumentDTO dto = new HealthCenterDocumentDTO();
            dto.setCenterId(centerId);

            // Upload du logo si fourni
            if (logo != null && !logo.isEmpty()) {
                String logoUrl = FileTransferUtil.handleFileUpload(logo);
                dto.setLogoUrl(logoUrl);
            }

            // Upload des documents justificatifs (plusieurs fichiers possibles)
            if (documents != null && !documents.isEmpty()) {
                // Vérifie que chaque fichier n'est pas vide
                for (MultipartFile file : documents) {
                    if (file.isEmpty()) {
                        return ResponseEntity.badRequest()
                                .body("One of the files is empty");
                    }
                }

                List<String> documentUrls = FileTransferUtil.uploadPictures(documents);
                // Convertit la liste en chaîne séparée par des virgules
                dto.setDocumentUrl(String.join(",", documentUrls));
            }

            HealthCenterDocumentDTO saved = healthCenterDocumentService.addHealthCenterDocument(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload failed: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error: " + e.getMessage());
        }
    }

    // Récupérer tous les documents d'un centre
    @Operation(
            summary = "Get all documents of a health center",
            description = "Retrieve the logo and all supporting documents (PDF, images) uploaded for a specific health center by its ID"
    )
    @GetMapping("/health-center-documents/{centerId}")
    public ResponseEntity<List<HealthCenterDocumentDTO>> getDocuments(@PathVariable Long centerId) {
        List<HealthCenterDocumentDTO> documents = healthCenterDocumentService.getDocumentsByHealthCenter(centerId);
        return ResponseEntity.ok(documents);
    }

}
