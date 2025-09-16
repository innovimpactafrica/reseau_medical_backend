package com.example.rml.back_office_rml.dto;

import com.example.rml.back_office_rml.enums.UserRole;
import com.example.rml.back_office_rml.enums.UserStatus;
import com.example.rml.back_office_rml.enums.MedicalSpecialty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RequestContainerDto {
    private Long userId;
    private String email;
    private UserRole role;
    private UserStatus status;
    private LocalDateTime creationDate;

    // Informations spécifiques au médecin (nullable si c'est un centre de santé)
    private Long doctorId;
    private String lastName;
    private String firstName;
    private MedicalSpecialty specialty;
    private String phone;
    private boolean hasPhoto;
    private boolean hasDocuments;

    // Informations spécifiques au centre de santé (nullable si c'est un médecin)
    private Long centerId;
    private String name;
    private String address;
    private String openingHours;
    private String contactPerson;
    private String contactPhone;
    private boolean hasLogo;
    private boolean hasCenterDocuments;
}
