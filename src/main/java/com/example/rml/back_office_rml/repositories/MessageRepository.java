package com.example.rml.back_office_rml.repositories;

import com.example.rml.back_office_rml.entities.Message;
import com.example.rml.back_office_rml.entities.Doctor;
import com.example.rml.back_office_rml.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository JPA pour la gestion des messages.
 */
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Récupère tous les messages envoyés ou reçus par un médecin.
     */
    @Query("""
        SELECT m FROM Message m
        WHERE (m.senderDoctor = :doctor OR m.receiverDoctor = :doctor)
        ORDER BY m.createdAt ASC
    """)
    List<Message> findAllByDoctor(@Param("doctor") Doctor doctor);

    /**
     * Récupère tous les messages envoyés ou reçus par un patient.
     */
    @Query("""
        SELECT m FROM Message m
        WHERE (m.senderPatient = :patient OR m.receiverPatient = :patient)
        ORDER BY m.createdAt ASC
    """)
    List<Message> findAllByPatient(@Param("patient") Patient patient);
}
