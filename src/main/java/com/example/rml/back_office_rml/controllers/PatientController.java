package com.example.rml.back_office_rml.controllers;

import com.example.rml.back_office_rml.dto.PatientDTO;
import com.example.rml.back_office_rml.services.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour gérer les patients via RequestParams
 */
@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    /**
     * Endpoint pour créer un patient en passant les infos via request parameters
     */
    @PostMapping("/create")
    public ResponseEntity<PatientDTO> createPatient(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String phoneNumber,
            @RequestParam (required = false)String email ,
            @RequestParam(required = false) String address
    ) {
        try {
            // Création du DTO à partir des params
            PatientDTO dto = new PatientDTO();
            dto.setFirstName(firstName);
            dto.setLastName(lastName);
            dto.setPhoneNumber(phoneNumber);
            dto.setEmail(email);
            dto.setAddress(address);

            // Enregistrement via le service
            PatientDTO savedPatient = patientService.registerPatient(dto);
            return new ResponseEntity<>(savedPatient, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            // Retourne 400 si une validation échoue
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Endpoint pour récupérer un patient par numéro de téléphone
     */
    @GetMapping("/phone")
    public ResponseEntity<PatientDTO> getPatientByPhone(@RequestParam String phoneNumber) {
        try {
            PatientDTO patientDTO = patientService.getPatientByPhoneNumber(phoneNumber);
            return new ResponseEntity<>(patientDTO, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint pour lister tous les patients
     */
    @GetMapping
    public ResponseEntity<List<PatientDTO>> getAllPatients() {
        List<PatientDTO> patients = patientService.getAllPatients();
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }
}
