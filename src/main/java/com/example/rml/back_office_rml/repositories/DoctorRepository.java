package com.example.rml.back_office_rml.repositories;

import com.example.rml.back_office_rml.entities.Doctor;
import com.example.rml.back_office_rml.enums.MedicalSpecialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    //lister les medecins par spécilaité
    List <Doctor> findBySpecialty(MedicalSpecialty specialty);


}
