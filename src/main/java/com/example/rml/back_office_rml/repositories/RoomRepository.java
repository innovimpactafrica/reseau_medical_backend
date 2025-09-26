package com.example.rml.back_office_rml.repositories;

import com.example.rml.back_office_rml.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    // Vérifier si une salle avec ce nom existe déjà dans un centre
    boolean existsByNameAndHealthCenter_CenterId(String name, Long healthCenterId);

    // Trouver toutes les salles d'un centre de santé
    List<Room> findByHealthCenter_CenterId(Long healthCenterId);
}
