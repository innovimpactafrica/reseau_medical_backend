package com.example.rml.back_office_rml.repositories;

import com.example.rml.back_office_rml.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    //Rechercher un patient par son numéro de téléphone
    Optional<Patient> findByPhoneNumber(String phoneNumber);

    //vérifier si le numéro n'est pas déjà enregistré
    boolean existsByPhoneNumber( String phoneNumber);
}
