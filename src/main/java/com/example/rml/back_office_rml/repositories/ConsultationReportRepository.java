package com.example.rml.back_office_rml.repositories;

import com.example.rml.back_office_rml.entities.ConsultationReport;
import com.example.rml.back_office_rml.enums.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultationReportRepository extends JpaRepository<ConsultationReport, Long> {

    // Récupérer tous les comptes rendus d'un dossier médical
    List<ConsultationReport> findByMedicalRecord_Id(Long recordId);


    // Récupérer les comptes rendus rédigés par un médecin
    List<ConsultationReport> findByDoctor_DoctorId(Long doctorId);

    // Récupérer les comptes rendus par type
    List<ConsultationReport> findByType(ReportType type);

    // Récupérer les comptes rendus par catégorie
    List<ConsultationReport> findByCategory(String category);


    // Récupérer les comptes rendus entre deux dates
    List<ConsultationReport> findByReportDateBetween(LocalDate startDate, LocalDate endDate);


}