package com.example.rml.back_office_rml.controllers;

import com.example.rml.back_office_rml.dto.RegisterHealthCenterDTO;
import com.example.rml.back_office_rml.services.RegisterHealthCenterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/healthcenter")
public class RegisterHealthCenterController {


    private final RegisterHealthCenterService registerHealthCenterService;

    public RegisterHealthCenterController (RegisterHealthCenterService registerHealthCenterService){
        this.registerHealthCenterService = registerHealthCenterService;
    }

    @Operation(summary = "Register a new health center")
    @PostMapping(consumes = "multipart/form-data")
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
