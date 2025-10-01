package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.RequestContainerDto;
import com.example.rml.back_office_rml.dto.RequestDoctorDTO;
import com.example.rml.back_office_rml.dto.RequestHealthCenterDTO;
import com.example.rml.back_office_rml.dto.RequestStatsDTO;
import com.example.rml.back_office_rml.entities.Doctor;
import com.example.rml.back_office_rml.entities.HealthCenter;
import com.example.rml.back_office_rml.entities.Users;
import com.example.rml.back_office_rml.enums.MedicalSpecialty;
import com.example.rml.back_office_rml.enums.UserRole;
import com.example.rml.back_office_rml.enums.UserStatus;
import com.example.rml.back_office_rml.repositories.DoctorRepository;
import com.example.rml.back_office_rml.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestContainerServiceImpl implements RequestContainerService {


    private final  UserRepository userRepository;
    private final DoctorRepository doctorRepository;

    public RequestContainerServiceImpl (UserRepository userRepository, DoctorRepository doctorRepository){
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
    }


  private static final List <UserRole> REQUEST_ROLES = Arrays.asList(
          UserRole.DOCTOR , UserRole.HEALTH_CENTER);


    @Override
    public List<RequestContainerDto> listAllRequests() {
        // Récupère tous les utilisateurs et les convertit en DTO
        List<Users> allUsers = userRepository.findAll();
        return allUsers.stream()
                .map(this::convertToRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestDoctorDTO> listDoctorRequests() {
        // Récupère tous les utilisateurs avec rôle DOCTOR
        List<Users> doctorRequests = userRepository.findByRole(UserRole.DOCTOR);

        return doctorRequests.stream()
                .map(this::convertToDoctorRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestHealthCenterDTO> listHealthCenterRequests() {
        // On récupère tous les utilisateurs ayant le rôle HEALTH_CENTER
        List<Users> healthCenterUsers = userRepository.findByRole(UserRole.HEALTH_CENTER);

        // On transforme chaque utilisateur en RequestHealthCenterDTO
        return healthCenterUsers.stream()
                .map(this::convertToHealthCenterDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestContainerDto> listRequestsByStatus(UserStatus status) {
        List<Users> usersList = userRepository.findByStatus(status);
        return usersList.stream()
                .map(this::convertToRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestContainerDto> listRequestsByRoleAndStatus(UserRole role, UserStatus status) {
        List<Users> usersByRoleAndStatus = userRepository.findByRoleAndStatus(role, status);
        return usersByRoleAndStatus.stream()
                .map(this::convertToRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestDoctorDTO> listDoctorRequestsByStatus(UserStatus status) {
        // Récupère tous les utilisateurs qui sont des médecins et qui ont le statut demandé
        List<Users> doctorsByStatus = userRepository.findByRoleAndStatus(UserRole.DOCTOR, status);

        // Convertit la liste Users en liste RequestDoctorDTO
        return doctorsByStatus.stream()
                .map(this::convertToDoctorRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestHealthCenterDTO> listHealthCenterRequestsByStatus(UserStatus status) {
        // Récupère tous les utilisateurs qui sont des centres de santé et qui ont le statut demandé
        List<Users> centersByStatus = userRepository.findByRoleAndStatus(UserRole.HEALTH_CENTER, status);

        // Convertit la liste Users en liste RequestHealthCenterDTO
        return centersByStatus.stream()
                .map(this::convertToHealthCenterDTO)
                .toList();
    }


    public RequestStatsDTO getRegistrationRequestStats() {

        RequestStatsDTO stats = new RequestStatsDTO();

        // Totaux globaux (DOCTOR + HEALTH_CENTER)
        stats.setTotalRequests(userRepository.countByRoleIn(REQUEST_ROLES));
        stats.setTotalPending(userRepository.countByRoleInAndStatus(REQUEST_ROLES, UserStatus.PENDING));
        stats.setTotalApproved(userRepository.countByRoleInAndStatus(REQUEST_ROLES, UserStatus.APPROVED));
        stats.setTotalRefused(userRepository.countByRoleInAndStatus(REQUEST_ROLES, UserStatus.REFUSED));

        // Statistiques spécifiques aux médecins
        stats.setTotalDoctorRequests(userRepository.countByRole(UserRole.DOCTOR));
        stats.setTotalDoctorPending(userRepository.countByRoleAndStatus(UserRole.DOCTOR, UserStatus.PENDING));
        stats.setTotalDoctorApproved(userRepository.countByRoleAndStatus(UserRole.DOCTOR, UserStatus.APPROVED));
        stats.setTotalDoctorRefused(userRepository.countByRoleAndStatus(UserRole.DOCTOR, UserStatus.REFUSED));

        // Statistiques spécifiques aux centres de santé
        stats.setTotalHealthCenterRequests(userRepository.countByRole(UserRole.HEALTH_CENTER));
        stats.setTotalHealthCenterPending(userRepository.countByRoleAndStatus(UserRole.HEALTH_CENTER, UserStatus.PENDING));
        stats.setTotalHealthCenterApproved(userRepository.countByRoleAndStatus(UserRole.HEALTH_CENTER, UserStatus.APPROVED));
        stats.setTotalHealthCenterRefused(userRepository.countByRoleAndStatus(UserRole.HEALTH_CENTER, UserStatus.REFUSED));

        return stats;
    }

    @Override
    public RequestContainerDto approveRequest(Long userId) {
        // On approuve la demande en appelant la méthode générique
        return changeRequestStatus(userId, UserStatus.APPROVED);

    }

    @Override
    public RequestContainerDto refuseRequest(Long userId) {
        // On refuse la demande en appelant la méthode générique
        return changeRequestStatus(userId, UserStatus.REFUSED);
    }

    @Override
    public RequestContainerDto changeRequestStatus(Long userId, UserStatus newStatus) {
        // Vérifier que l'utilisateur existe
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        //Vérifier que l'utilisateur a un rôle valide (DOCTOR ou HEALTH_CENTER)
        if (user.getRole() != UserRole.DOCTOR && user.getRole() != UserRole.HEALTH_CENTER) {
            throw new RuntimeException("Impossible : rôle non autorisé pour cette opération");
        }

        // Vérifier que le statut actuel permet le changement
        if (!canChangeStatus(user.getStatus(), newStatus)) {
            throw new RuntimeException("Impossible de changer le statut : la demande n'est pas en PENDING");
        }

        //Appliquer le nouveau statut
        user.setStatus(newStatus);
        Users savedUser = userRepository.save(user);

        // Retourner le DTO mis à jour
        return convertToRequestDto(savedUser);
    }


    /**
     * Vérifie si le changement de statut est autorisé
     *
     * @param currentStatus statut actuel
     * @param newStatus nouveau statut demandé
     * @return true si le changement est autorisé
     */
    private boolean canChangeStatus(UserStatus currentStatus, UserStatus newStatus) {
        // Pour approuver ou refuser : le statut doit être PENDING
        if (newStatus == UserStatus.APPROVED || newStatus == UserStatus.REFUSED) {
            return currentStatus == UserStatus.PENDING;
        }

        // Pour d'autres changements de statut, on peut être plus flexible
        // (par exemple, passer de APPROVED à ACTIVE, etc.)
        return true;
    }


    // Conversion d'un utilisateur vers RequestContainerDto
    private RequestContainerDto convertToRequestDto(Users user) {
        RequestContainerDto dto = new RequestContainerDto();
        dto.setUserId(user.getUserId());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());
        dto.setCreationDate(user.getCreationDate());

        // Vérifie si l'utilisateur est un médecin et si l'objet Doctor n'est pas null
        if (user.getRole() == UserRole.DOCTOR && user.getDoctor() != null) {
            Doctor doctor = user.getDoctor(); // Récupère les infos du médecin
            dto.setDoctorId(doctor.getDoctorId());
            dto.setLastName(doctor.getLastName());
            dto.setFirstName(doctor.getFirstName());
            dto.setSpecialty(doctor.getSpecialty());
            dto.setPhone(doctor.getPhone());
            dto.setHasPhoto(doctor.getPhoto() != null);
            dto.setHasDocuments(doctor.getDocuments() != null);
        }

        // Vérifie si l'utilisateur est un centre de santé et si l'objet HealthCenter n'est pas null
        if (user.getRole() == UserRole.HEALTH_CENTER && user.getHealthCenter() != null) {
            HealthCenter center = user.getHealthCenter(); // Récupère les infos du centre
            dto.setCenterId(center.getCenterId());
            dto.setName(center.getName());
            dto.setAddress(center.getAddress());
            dto.setOpeningHours(center.getOpeningHours());
            dto.setContactPerson(center.getContactPerson());
            dto.setContactPhone(center.getContactPhone());
            dto.setHasLogo(center.getLogo() != null);
            dto.setHasCenterDocuments(center.getDocuments() != null);
        }

        return dto;
    }

    // Conversion d'un utilisateur vers RequestDoctorDTO
    private RequestDoctorDTO convertToDoctorRequestDto(Users user) {
        RequestDoctorDTO dto = new RequestDoctorDTO();

        dto.setUserId(user.getUserId());
        dto.setEmail(user.getEmail());
        dto.setStatus(user.getStatus());
        dto.setDateOfRequest(user.getCreationDate());

        // Vérifie si l'utilisateur est un médecin et si l'objet Doctor n'est pas null
        if (user.getRole() == UserRole.DOCTOR && user.getDoctor() != null) {
            Doctor doctor = user.getDoctor(); // Récupère les infos du médecin
            dto.setDoctorId(doctor.getDoctorId());
            dto.setLastName(doctor.getLastName());
            dto.setFirstName(doctor.getFirstName());
            dto.setSpecialty(doctor.getSpecialty());
            dto.setPhone(doctor.getPhone());
            dto.setHasPhoto(doctor.getPhoto() != null);
            dto.setHasDocuments(doctor.getDocuments() != null);
        }
        return dto;
    }

    // --- Méthode utilitaire pour convertir Users en RequestHealthCenterDTO ---
    private RequestHealthCenterDTO convertToHealthCenterDTO(Users user) {
        RequestHealthCenterDTO dto = new RequestHealthCenterDTO();
        dto.setUserId(user.getUserId());
        dto.setEmail(user.getEmail());
        dto.setStatus(user.getStatus());
        dto.setDateOfRequest(user.getCreationDate());

        // Vérifie que l'utilisateur est un centre de santé et qu'il est associé
        if (user.getRole() == UserRole.HEALTH_CENTER && user.getHealthCenter() != null) {
            HealthCenter center = user.getHealthCenter();
            dto.setCenterId(center.getCenterId());
            dto.setName(center.getName());
            dto.setAddress(center.getAddress());
            dto.setOpeningHours(center.getOpeningHours());
            dto.setContactPerson(center.getContactPerson());
            dto.setContactPhone(center.getContactPhone());
            dto.setHasLogo(center.getLogo() != null);
            dto.setHasCenterDocuments(center.getDocuments() != null);
        }

        return dto;
    }

}
