package com.example.rml.back_office_rml.repositories;

import com.example.rml.back_office_rml.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UtilisateurRepository extends JpaRepository <Utilisateur, Long> {
}
