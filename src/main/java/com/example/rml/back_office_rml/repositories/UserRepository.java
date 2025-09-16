package com.example.rml.back_office_rml.repositories;

import com.example.rml.back_office_rml.entities.Users;
import com.example.rml.back_office_rml.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    // Rechercher un utilisateur par email
    Optional<Users> findByEmail(String email);

    // Récupérer tous les utilisateurs par rôle (DOCTOR, HEALTH_CENTER)
    List<Users> findByRole(UserRole role);
}
