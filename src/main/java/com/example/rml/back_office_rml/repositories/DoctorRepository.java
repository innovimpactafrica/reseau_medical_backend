package com.example.rml.back_office_rml.repositories;

import com.example.rml.back_office_rml.entities.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {



}
