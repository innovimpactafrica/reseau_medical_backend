package com.example.rml.back_office_rml.repositories;

import com.example.rml.back_office_rml.entities.ConsultationDuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultationDurationRepository extends JpaRepository<ConsultationDuration, Long> {

    // Recherche d'une durée spécifique en fonction des minutes
    Optional<ConsultationDuration> findByMinutes(Integer minutes);

    // Liste uniquement les durées actives
    List<ConsultationDuration> findByActiveTrue();

}
