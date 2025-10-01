// RequestContainerController.java
package com.example.rml.back_office_rml.controllers;

import com.example.rml.back_office_rml.dto.*;
import com.example.rml.back_office_rml.enums.MedicalSpecialty;
import com.example.rml.back_office_rml.enums.UserRole;
import com.example.rml.back_office_rml.enums.UserStatus;
import com.example.rml.back_office_rml.services.RegisterDoctorService;
import com.example.rml.back_office_rml.services.RegisterHealthCenterService;
import com.example.rml.back_office_rml.services.RequestContainerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class RequestContainerController {


    private final RequestContainerService requestContainerService;
    private final RegisterDoctorService registerDoctorService;
    private final RegisterHealthCenterService registerHealthCenterService;

    public RequestContainerController (RequestContainerService requestContainerService , RegisterDoctorService registerDoctorService , RegisterHealthCenterService registerHealthCenterService ) {
        this.requestContainerService =requestContainerService;
        this.registerDoctorService = registerDoctorService;
        this.registerHealthCenterService= registerHealthCenterService;
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
            dto.setPhoto(photo);
            dto.setDocuments(documents);

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
            dto.setLogo(logo);
            dto.setDocuments(documents);


            // Call the service
            RegisterHealthCenterDTO result = registerHealthCenterService.registerHealthCenter(dto);

            // Return response
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

}
