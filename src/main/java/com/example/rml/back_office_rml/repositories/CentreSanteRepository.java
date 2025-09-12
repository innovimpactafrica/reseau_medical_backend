package com.example.rml.back_office_rml.repositories;

import com.example.rml.back_office_rml.entities.CentreSante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CentreSanteRepository extends JpaRepository<CentreSante, Long> {

    Optional<CentreSante> findByUtilisateurEmail(String email);

    Optional<CentreSante> findByUtilisateurIdUtilisateur(Long idUtilisateur);

}
