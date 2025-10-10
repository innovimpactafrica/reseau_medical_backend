package com.example.rml.back_office_rml.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour représenter un message.
 * Sert à la fois pour l’envoi et la récupération des messages.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String senderDoctorFirstName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String senderDoctorLastName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String receiverDoctorFirstName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String receiverDoctorLastName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String receiverPatientFirstName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String receiverPatientLastName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String SenderPatientFirstName;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String SenderPatientLastName;

    private String content;           // Contenu du message

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;

    private Long senderDoctorId;      // ID du médecin expéditeur (si applicable)
    private Long senderPatientId;     // ID du patient expéditeur (si applicable)
    private Long receiverDoctorId;    // ID du médecin destinataire (si applicable)
    private Long receiverPatientId;   // ID du patient destinataire (si applicable)

}
