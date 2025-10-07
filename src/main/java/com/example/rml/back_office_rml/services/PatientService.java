package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.PatientDTO;

import java.util.List;

public interface PatientService {


    PatientDTO registerPatient(PatientDTO patientDTO);
    List<PatientDTO> getAllPatients();
    PatientDTO getPatientByPhoneNumber(String phoneNumber);

    //PatientDTO getPatientById(Long patientId);
    //PatientDTO updatePatient(Long patientId, PatientDTO patientDTO);

}
