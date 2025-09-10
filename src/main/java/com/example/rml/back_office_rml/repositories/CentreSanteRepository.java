package com.example.rml.back_office_rml.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CentreSanteRepository extends JpaRepository <CentreSanteRepository, Long> {
}
