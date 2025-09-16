package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.RequestContainerDto;
import com.example.rml.back_office_rml.dto.RequestDoctorDTO;
import com.example.rml.back_office_rml.dto.RequestHealthCenterDTO;
import com.example.rml.back_office_rml.entities.Doctor;
import com.example.rml.back_office_rml.entities.HealthCenter;
import com.example.rml.back_office_rml.entities.Users;
import com.example.rml.back_office_rml.enums.UserRole;
import com.example.rml.back_office_rml.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestContainerServiceImpl implements RequestContainerService {

    @Autowired
    private UserRepository userRepository;

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
        return dto;
    }

    // --- Méthode utilitaire pour convertir Users en RequestHealthCenterDTO ---
    private RequestHealthCenterDTO convertToHealthCenterDTO(Users user) {
        RequestHealthCenterDTO dto = new RequestHealthCenterDTO();
        dto.setUserId(user.getUserId());
        dto.setEmail(user.getEmail());
        dto.setStatus(user.getStatus());
        dto.setCreationDate(user.getCreationDate());

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
