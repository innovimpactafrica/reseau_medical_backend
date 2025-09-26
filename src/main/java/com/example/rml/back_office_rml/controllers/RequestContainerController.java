// RequestContainerController.java
package com.example.rml.back_office_rml.controllers;

import com.example.rml.back_office_rml.dto.RequestContainerDto;
import com.example.rml.back_office_rml.dto.RequestDoctorDTO;
import com.example.rml.back_office_rml.dto.RequestHealthCenterDTO;
import com.example.rml.back_office_rml.dto.RequestStatsDTO;
import com.example.rml.back_office_rml.enums.UserRole;
import com.example.rml.back_office_rml.enums.UserStatus;
import com.example.rml.back_office_rml.services.RequestContainerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class RequestContainerController {


    private final RequestContainerService requestContainerService;

    public RequestContainerController (RequestContainerService requestContainerService ){
        this.requestContainerService =requestContainerService;
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

}
