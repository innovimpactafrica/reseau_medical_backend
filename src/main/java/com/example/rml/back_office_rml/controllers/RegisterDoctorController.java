package com.example.rml.back_office_rml.controllers;

import com.example.rml.back_office_rml.dto.RegisterDoctorDTO;
import com.example.rml.back_office_rml.enums.MedicalSpecialty;
import com.example.rml.back_office_rml.services.RegisterDoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("api/doctor")
public class RegisterDoctorController {


    private final RegisterDoctorService registerDoctorService;

    public RegisterDoctorController (RegisterDoctorService registerDoctorService){
        this.registerDoctorService = registerDoctorService;
    }

    @Operation(summary = "Register a new doctor")
    @PostMapping(consumes = "multipart/form-data")
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
}
