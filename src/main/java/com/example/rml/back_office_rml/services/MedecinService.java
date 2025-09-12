package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.InscriptionMedecinDto;

import java.io.IOException;

public interface MedecinService {

    // Inscrire un nouveau médecin
    InscriptionMedecinDto inscrireMedecin(InscriptionMedecinDto inscriptionMedecinDto) throws IOException;
}
