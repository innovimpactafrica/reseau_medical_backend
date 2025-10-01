package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.RegisterDoctorDTO;
import com.example.rml.back_office_rml.dto.RequestDoctorDTO;
import com.example.rml.back_office_rml.enums.MedicalSpecialty;

import java.io.IOException;
import java.util.List;

public interface RegisterDoctorService {

    // Check if an email already exists
    boolean emailExists(String email);

    // Create a new doctor user
    RegisterDoctorDTO createDoctorUser(RegisterDoctorDTO registerDoctorDTO) throws IOException;

    //Get Doctor By Speciality
    List<RequestDoctorDTO> getDoctorsBySpecialty(MedicalSpecialty medicalSpecialty);
}
