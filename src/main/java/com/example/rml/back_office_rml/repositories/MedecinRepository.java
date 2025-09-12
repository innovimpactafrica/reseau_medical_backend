package com.example.rml.back_office_rml.repositories;

import com.example.rml.back_office_rml.entities.Medecin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedecinRepository extends JpaRepository<Medecin, Long> {


    // Trouver un médecin par l'ID de l'utilisateur référent
    Optional<Medecin> findByUtilisateurIdUtilisateur(Long idUtilisateur);
}
