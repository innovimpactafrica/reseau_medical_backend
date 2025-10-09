package com.example.rml.back_office_rml.repositories;

import com.example.rml.back_office_rml.entities.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    // Récupérer toutes les ordonnances d'un dossier médical
    List<Prescription> findByMedicalRecord_Id(Long recordId);

    // Récupérer les ordonnances prescrites par un médecin
    List<Prescription> findByDoctor_DoctorId(Long doctorId);

    // Récupérer les ordonnances d'un rendez-vous
    Optional<Prescription> findByAppointment_AppointmentId(Long appointmentId);

    // Récupérer les ordonnances d'un rendez-vous
    Optional<Prescription> findByPrescriptionNumber(String prescriptionNumber);

    //Cette requête ne renverra que les ordonnances non expirées dont validUntil est dépassé utiliser pour automatiser l'expiration des dates d'ordonnance (PrescriptionStatusScheduler)
    @Query("SELECT p FROM Prescription p WHERE p.status <> com.example.rml.back_office_rml.enums.PrescriptionStatus.EXPIRED AND p.validUntil < :today")
    List<Prescription> findExpiredPrescriptions(@Param("today") LocalDate today);
}