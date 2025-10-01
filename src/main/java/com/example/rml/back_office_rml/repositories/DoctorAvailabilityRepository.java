package com.example.rml.back_office_rml.repositories;

import com.example.rml.back_office_rml.entities.DoctorAvailability;
import com.example.rml.back_office_rml.enums.DayOfWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {

    // Vérifier les conflits d'horaire pour un médecin
    @Query("SELECT da FROM DoctorAvailability da WHERE " +
            "da.doctor.doctorId = :doctorId AND " +
            "da.healthCenter.centerId = :healthCenterId AND " +
            "da.dayOfWeek = :dayOfWeek AND " +
            "da.active = true AND " +
            "((da.startTime < :endTime AND da.endTime > :startTime))")
    List<DoctorAvailability> findConflictingAvailabilities(
            @Param("doctorId") Long doctorId,
            @Param("healthCenterId") Long healthCenterId,
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    // Récupérer toutes les disponibilités d’un médecin
    List<DoctorAvailability> findByDoctorDoctorId(Long doctorId);

    // Récupérer toutes les disponibilités d’un centre de santé
    List<DoctorAvailability> findByHealthCenterCenterId(Long healthCenterId);

    // Récupérer uniquement les disponibilités actives d’un médecin
    List<DoctorAvailability> findByDoctorDoctorIdAndActiveTrue(Long doctorId);
}
