package com.example.rml.back_office_rml.repositories;

import com.example.rml.back_office_rml.entities.Slot;
import com.example.rml.back_office_rml.enums.DayOfWeek;
import com.example.rml.back_office_rml.enums.MedicalSpecialty;
import com.example.rml.back_office_rml.enums.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Long> {

    // ========================================================================
    // VÉRIFICATIONS DE CONFLITS POUR DATE SPÉCIFIQUE
    // ========================================================================

    /**
     * Vérifie s’il existe déjà un créneau (Slot) pour un médecin à une date donnée
     * qui chevauche le nouveau créneau proposé.
     *
     *  Détails :
     * - Cette requête retourne TRUE s’il existe au moins un autre créneau du médecin
     *   à la même date qui se superpose partiellement ou totalement à celui qu’on veut créer.
     *
     * - Logique de chevauchement :
     *   (s.startTime < :endTime AND s.endTime > :startTime)
     *   → Cela signifie que le créneau existant commence avant que le nouveau ne se termine,
     *     et qu’il se termine après que le nouveau ait commencé.
     *     Donc, les deux créneaux se croisent dans le temps.
     *
     * slotId permet d’exclure le créneau en cours de modification (utile en mise à jour).Si slotId est null, on vérifie tous les slots (création) ; sinon, si c’est égal au slot qu’on modifie, on l’ignore pour ne pas se comparer à soi-même
     */
    @Query("SELECT COUNT(s) > 0 FROM Slot s WHERE s.doctor.doctorId = :doctorId " +
            "AND s.slotDate = :slotDate " +
            "AND (s.startTime < :endTime AND s.endTime > :startTime) " +
            "AND (:slotId IS NULL OR s.slotId != :slotId)")
    boolean existsOverlappingSlotForDoctorOnDate(
            @Param("doctorId") Long doctorId,
            @Param("slotDate") LocalDate slotDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("slotId") Long slotId);

    /**
     * Vérifie si une salle est déjà occupée à une date spécifique.
     * On ignore le slot en cours de modification (slotId) pour ne pas se comparer à lui-même.
     */
    @Query("SELECT COUNT(s) > 0 FROM Slot s WHERE s.room.roomId = :roomId " +
            "AND s.slotDate = :slotDate " +
            "AND (s.startTime < :endTime AND s.endTime > :startTime) " +
            "AND (:slotId IS NULL OR s.slotId != :slotId)")
    boolean existsOverlappingSlotForRoomOnDate(
            @Param("roomId") Long roomId,
            @Param("slotDate") LocalDate slotDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("slotId") Long slotId);


    // ========================================================================
    // VÉRIFICATIONS DE CONFLITS POUR CRÉNEAUX RÉCURRENTS
    // ========================================================================

    /**
     * Vérifie si un médecin a déjà un créneau récurrent ce jour de la semaine.
     * On ignore le slot en cours de modification (slotId) pour ne pas se comparer à lui-même.
     */
    @Query("SELECT COUNT(s) > 0 FROM Slot s WHERE s.doctor.doctorId = :doctorId " +
            "AND s.isRecurring = true " +
            "AND s.dayOfWeek = :dayOfWeek " +
            "AND (s.startTime < :endTime AND s.endTime > :startTime) " +
            "AND (:slotId IS NULL OR s.slotId != :slotId)")
    boolean existsOverlappingRecurringSlotForDoctor(
            @Param("doctorId") Long doctorId,
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("slotId") Long slotId);


    /**
     * Vérifie si une salle a déjà un créneau récurrent ce jour de la semaine
     */
    @Query("SELECT COUNT(s) > 0 FROM Slot s WHERE s.room.roomId = :roomId " +
            "AND s.isRecurring = true " +
            "AND s.dayOfWeek = :dayOfWeek " +
            "AND ((:startTime >= s.startTime AND :startTime < s.endTime) " +
            "OR (:endTime > s.startTime AND :endTime <= s.endTime) " +
            "OR (:startTime <= s.startTime AND :endTime >= s.endTime)) " +
            "AND (:slotId IS NULL OR s.slotId != :slotId)")
    boolean existsOverlappingRecurringSlotForRoom(
            @Param("roomId") Long roomId,
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("slotId") Long slotId);



    // ========================================================================
    // RÉCUPÉRATION DES SLOTS
    // ========================================================================
    // Récupère tous les créneaux d’un médecin triés par date puis heure de début
    List<Slot> findByDoctor_DoctorIdOrderBySlotDateAscStartTimeAsc(Long doctorId);

    // Récupère tous les créneaux d’une salle triés par date puis heure de début
    List<Slot> findByRoom_RoomIdOrderBySlotDateAscStartTimeAsc(Long roomId);

    // Récupère tous les créneaux de toutes les salles d’un centre de santé triés par date puis heure de début
    List<Slot> findByRoom_HealthCenter_CenterIdOrderBySlotDateAscStartTimeAsc(Long healthCenterId);

    // Récupère tous les créneaux avec un statut spécifique (ex: AVAILABLE, OCCUPIED) triés par date puis heure de début
    List<Slot> findByStatusOrderBySlotDateAscStartTimeAsc(SlotStatus status);

    /**
     * Récupère tous les slots disponibles (status = AVAILABLE) pour une spécialité médicale donnée,
     * triés par date puis heure de début.
     */
    @Query("SELECT s FROM Slot s " +
            "WHERE s.status = 'AVAILABLE' " +
            "AND s.doctor.specialty = :specialty " +
            "ORDER BY s.slotDate ASC, s.startTime ASC")
    List<Slot> findAvailableSlotsBySpecialty(@Param("specialty") MedicalSpecialty specialty);
}


