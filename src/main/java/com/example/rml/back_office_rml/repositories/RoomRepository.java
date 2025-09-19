package com.example.rml.back_office_rml.repositories;

import com.example.rml.back_office_rml.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    // Vérifier si une salle avec ce nom existe déjà dans un centre
    boolean existsByNameAndHealthCenter_CenterId(String name, Long healthCenterId);



}
