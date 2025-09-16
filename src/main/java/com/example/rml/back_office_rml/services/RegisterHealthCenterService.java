package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.RegisterHealthCenterDTO;
import com.example.rml.back_office_rml.entities.HealthCenter;
import org.w3c.dom.stylesheets.LinkStyle;

import java.io.IOException;
import java.util.List;

public interface RegisterHealthCenterService
{
    // Check if an email already exists
    boolean emailExists(String email);

    // Create a new HealthCenter
    RegisterHealthCenterDTO registerHealthCenter(RegisterHealthCenterDTO registerHealthCenterDTO) throws IOException;


}
