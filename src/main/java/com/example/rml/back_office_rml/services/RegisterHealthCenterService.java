package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.RegisterHealthCenterDTO;
import java.io.IOException;

public interface RegisterHealthCenterService
{
    // Check if an email already exists
    boolean emailExists(String email);

    // Create a new HealthCenter
    RegisterHealthCenterDTO registerHealthCenter(RegisterHealthCenterDTO registerHealthCenterDTO) throws IOException;


}
