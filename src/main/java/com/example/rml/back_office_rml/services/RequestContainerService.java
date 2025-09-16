package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.RequestContainerDto;
import com.example.rml.back_office_rml.dto.RequestDoctorDTO;
import com.example.rml.back_office_rml.dto.RequestHealthCenterDTO;

import java.util.List;

public interface RequestContainerService {

    /**
     * Récupère toutes les demandes (médecins et centres de santé)
     */
    List<RequestContainerDto> listAllRequests();

    /**
     * Récupère uniquement les demandes des médecins
     */
    List<RequestDoctorDTO> listDoctorRequests();

    /**
     * Récupère uniquement les demandes des centres de santé
     */
    List<RequestHealthCenterDTO> listHealthCenterRequests();

}
