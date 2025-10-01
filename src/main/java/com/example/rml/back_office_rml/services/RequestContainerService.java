package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.RequestContainerDto;
import com.example.rml.back_office_rml.dto.RequestDoctorDTO;
import com.example.rml.back_office_rml.dto.RequestHealthCenterDTO;
import com.example.rml.back_office_rml.dto.RequestStatsDTO;
import com.example.rml.back_office_rml.enums.MedicalSpecialty;
import com.example.rml.back_office_rml.enums.UserRole;
import com.example.rml.back_office_rml.enums.UserStatus;

import java.util.List;

public interface RequestContainerService {

    /**
     * Récupère toutes les demandes (médecins et centres de santé)
     */
    List<RequestContainerDto> listAllRequests();

    /**
     * Récupère uniquement les demandes des médecins
     */
    List<RequestDoctorDTO> listDoctorRequests();

    /**
     * Récupère uniquement les demandes des centres de santé
     */
    List<RequestHealthCenterDTO> listHealthCenterRequests();

    /**
     * Récupère les demandes par statut
     */
    List<RequestContainerDto> listRequestsByStatus(UserStatus status);

    /**
     * Récupère les demandes par rôle et statut
     */
    List<RequestContainerDto> listRequestsByRoleAndStatus(UserRole role, UserStatus status);


    /**
     * Récupère les demandes par statut de "médecin"
     *
     */
    List<RequestDoctorDTO> listDoctorRequestsByStatus(UserStatus status);

    /**
     *  Récupère les demandes par statut de "Centre de santé"
     */
    List<RequestHealthCenterDTO> listHealthCenterRequestsByStatus(UserStatus status);

    /**
     * Récupère les statistiques détaillées des demandes d'inscription.
     * Inclut les totaux globaux et les détails par rôle (médecins et centres de santé)
     * répartis par statut (PENDING, APPROVED, REFUSED).
     */
    RequestStatsDTO getRegistrationRequestStats();

    /**
     * Approuve une demande d'inscription en passant le statut de PENDING à APPROVED
     *
     * @param userId l'ID de l'utilisateur dont la demande doit être approuvée
     * @return RequestContainerDto mis à jour
     * @throws RuntimeException si l'utilisateur n'existe pas ou si le statut n'est pas PENDING
     */
    RequestContainerDto approveRequest(Long userId);

    /**
     * Refuse une demande d'inscription en passant le statut de PENDING à REFUSED
     *
     * @param userId l'ID de l'utilisateur dont la demande doit être approuvée
     * @return RequestContainerDto mis à jour
     * @throws RuntimeException si l'utilisateur n'existe pas ou si le statut n'est pas PENDING
     */
    RequestContainerDto  refuseRequest(Long userId);

    /**
     * Modifie le statut d'une demande d'inscription pour un utilisateur.
     * Vérifie que l'utilisateur existe, que son rôle est valide (DOCTOR ou HEALTH_CENTER),
     * et que le changement de statut est autorisé (ex : PENDING → APPROVED/REFUSED),
     * puis sauvegarde le nouvel état et retourne le DTO mis à jour.
     *
     * @param userId l'ID de l'utilisateur
     * @param newStatus le nouveau statut à appliquer
     * @return RequestContainerDto mis à jour
     * @throws RuntimeException si l'utilisateur n'existe pas, si le rôle n'est pas autorisé,
     *         ou si le changement de statut n'est pas valide
     */
    RequestContainerDto changeRequestStatus(Long userId, UserStatus newStatus);



}
