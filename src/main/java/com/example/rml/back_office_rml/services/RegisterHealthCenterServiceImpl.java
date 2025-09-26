package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.RegisterHealthCenterDTO;
import com.example.rml.back_office_rml.entities.HealthCenter;
import com.example.rml.back_office_rml.entities.Users;
import com.example.rml.back_office_rml.enums.UserRole;
import com.example.rml.back_office_rml.enums.UserStatus;
import com.example.rml.back_office_rml.repositories.HealthCenterRepository;
import com.example.rml.back_office_rml.repositories.UserRepository;
import org.springframework.stereotype.Service;
import java.io.IOException;



@Service
public class RegisterHealthCenterServiceImpl implements RegisterHealthCenterService {

    private final HealthCenterRepository healthCenterRepository;
    private final UserRepository userRepository;

    public RegisterHealthCenterServiceImpl  (HealthCenterRepository healthCenterRepository, UserRepository userRepository){
        this.healthCenterRepository = healthCenterRepository;
        this.userRepository = userRepository;
    }


    @Override
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public RegisterHealthCenterDTO registerHealthCenter(RegisterHealthCenterDTO registerHealthCenterDTO) throws IOException {

        // 1. Vérifier si l'email existe déjà
        if (emailExists(registerHealthCenterDTO.getEmail())) {
            throw new RuntimeException("A user with this email already exists : " + registerHealthCenterDTO.getEmail());
        }

        // 2. Créer l'utilisateur de base
        Users user = new Users();
        user.setEmail(registerHealthCenterDTO.getEmail());
        user.setPassword(registerHealthCenterDTO.getPassword()); // TODO: le mot de passe à hacher
        user.setRole(UserRole.HEALTH_CENTER);
        user.setStatus(UserStatus.PENDING);

        Users savedUser = userRepository.save(user);


        //3. Créer le centre de santé
        HealthCenter healthCenter = new HealthCenter();
        healthCenter.setUser(savedUser);
        healthCenter.setName(registerHealthCenterDTO.getHealthCenterName());
        healthCenter.setAddress(registerHealthCenterDTO.getHealthCenterAddress());
        healthCenter.setOpeningHours(registerHealthCenterDTO.getOpeningHours());
        healthCenter.setContactPerson(registerHealthCenterDTO.getReferentName());
        healthCenter.setContactPhone(registerHealthCenterDTO.getReferentPhone());

        if (registerHealthCenterDTO.getLogo() != null && !registerHealthCenterDTO.getLogo().isEmpty()) {
            healthCenter.setLogo(registerHealthCenterDTO.getLogo().getBytes());
        }
        if (registerHealthCenterDTO.getDocuments() != null && !registerHealthCenterDTO.getDocuments().isEmpty()) {
            healthCenter.setDocuments(registerHealthCenterDTO.getDocuments().getBytes());
        }

        HealthCenter savedHealthCenter = healthCenterRepository.save(healthCenter);

        //4. La réponse
        RegisterHealthCenterDTO response = new RegisterHealthCenterDTO();
        response.setHealthCenterName(savedHealthCenter.getName());
        response.setHealthCenterAddress(savedHealthCenter.getAddress());
        response.setOpeningHours(savedHealthCenter.getOpeningHours());
        response.setReferentName(savedHealthCenter.getContactPerson());
        response.setReferentPhone(savedHealthCenter.getContactPhone());
        response.setEmail(savedUser.getEmail());
        response.setPassword(savedUser.getPassword());
        response.setRole(savedUser.getRole());
        response.setHasLogo(savedHealthCenter.getLogo() != null);
        response.setHasDocuments(savedHealthCenter.getDocuments() != null);
        response.setStatus(savedUser.getStatus());


        return response;
    }

}
