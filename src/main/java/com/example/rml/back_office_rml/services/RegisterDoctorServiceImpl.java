package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.RegisterDoctorDTO;
import com.example.rml.back_office_rml.entities.Users;
import com.example.rml.back_office_rml.entities.Doctor;
import com.example.rml.back_office_rml.enums.UserRole;
import com.example.rml.back_office_rml.enums.UserStatus;
import com.example.rml.back_office_rml.repositories.UserRepository;
import com.example.rml.back_office_rml.repositories.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
public class RegisterDoctorServiceImpl implements RegisterDoctorService {

    private final UserRepository userRepository;

    private final DoctorRepository doctorRepository;

    public RegisterDoctorServiceImpl (UserRepository userRepository , DoctorRepository doctorRepository){
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
    }


    @Override
    public boolean emailExists(String email) {
        // Vérifie si un email existe déjà dans la base
        return userRepository.findByEmail(email).isPresent();
    }

    @Transactional
    @Override
    public RegisterDoctorDTO createDoctorUser(RegisterDoctorDTO dto) throws IOException {

        // 1. Vérifier si l'email existe déjà
        if (emailExists(dto.getEmail())) {
            throw new RuntimeException("A user with this email already exists: " + dto.getEmail());
        }

        // 2. Créer l'utilisateur de base
        Users user = new Users();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword()); // TODO: le mot de passe à hacher
        user.setRole(UserRole.DOCTOR);
        user.setStatus(UserStatus.PENDING);

        Users savedUser = userRepository.save(user);

        // 3. Créer l'entité Doctor
        Doctor doctor = new Doctor();
        doctor.setUser(savedUser);
        doctor.setLastName(dto.getLastName());
        doctor.setFirstName(dto.getFirstName());
        doctor.setSpecialty(dto.getSpecialty());
        doctor.setPhone(dto.getPhone());

        // Gestion des fichiers
        if (dto.getPhoto() != null && !dto.getPhoto().isEmpty()) {
            doctor.setPhoto(dto.getPhoto().getBytes());
        }
        if (dto.getDocuments() != null && !dto.getDocuments().isEmpty()) {
            doctor.setDocuments(dto.getDocuments().getBytes());
        }

        Doctor savedDoctor = doctorRepository.save(doctor);

        //4.Retourner le DTO avec les indicateurs de présence des fichiers
        RegisterDoctorDTO response = new RegisterDoctorDTO();

        response.setFirstName(savedDoctor.getFirstName());
        response.setLastName(savedDoctor.getLastName());
        response.setEmail(savedUser.getEmail());
        response.setPassword(savedUser.getPassword());
        response.setSpecialty(savedDoctor.getSpecialty());
        response.setPhone(savedDoctor.getPhone());
        response.setHasPhoto(savedDoctor.getPhoto() != null);
        response.setHasDocuments(savedDoctor.getDocuments() != null);
        response.setStatus(savedUser.getStatus());
        return response;
    }
}
