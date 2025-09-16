package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.RegisterDoctorDTO;

import java.io.IOException;

public interface RegisterDoctorService {

    // Check if an email already exists
    boolean emailExists(String email);

    // Create a new doctor user
    RegisterDoctorDTO createDoctorUser(RegisterDoctorDTO registerDoctorDTO) throws IOException;
}
