package com.example.rml.back_office_rml.repositories;

import com.example.rml.back_office_rml.entities.Users;
import com.example.rml.back_office_rml.enums.UserRole;
import com.example.rml.back_office_rml.enums.UserStatus;
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

    // Récupérer tous les utilisateurs par statut
    List<Users> findByStatus(UserStatus status);

    // Récupérer tous les utilisateurs par rôle et statut
    List<Users> findByRoleAndStatus(UserRole role, UserStatus status);


    //Compte les utilisateurs ayant l'un des rôles spécifiés
    long countByRoleIn(List<UserRole> roles);

    //Compte les utilisateurs ayant l'un des rôles spécifiés et un statut donné
    long countByRoleInAndStatus(List<UserRole> roles, UserStatus status);

    //Compte les utilisateurs par rôle et statut
    long countByRoleAndStatus (UserRole role, UserStatus status);

    //Compte les utilisateurs par rôle (pour les totaux par rôle)
    long countByRole(UserRole role);






}
