package com.example.rml.back_office_rml.repositories;


import com.example.rml.back_office_rml.entities.DoctorDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface DoctorDocumentRepository  extends JpaRepository <DoctorDocument, Long> {
    Optional <DoctorDocument> findByDoctor_DoctorId(Long doctorId);
}
