// RequestContainerController.java
package com.example.rml.back_office_rml.controllers;

import com.example.rml.back_office_rml.dto.RequestContainerDto;
import com.example.rml.back_office_rml.dto.RequestDoctorDTO;
import com.example.rml.back_office_rml.dto.RequestHealthCenterDTO;
import com.example.rml.back_office_rml.services.RequestContainerService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class RequestContainerController {

    @Autowired
    private RequestContainerService requestContainerService;

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
}
