package com.example.rml.back_office_rml.controllers;

import com.example.rml.back_office_rml.dto.ConversationDTO;
import com.example.rml.back_office_rml.dto.MessageDTO;
import com.example.rml.back_office_rml.entities.Doctor;
import com.example.rml.back_office_rml.entities.Patient;
import com.example.rml.back_office_rml.services.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des messages.
 * Permet d'envoyer un message et de récupérer les conversations d'un utilisateur.
 */
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @Getter
    public static class ErrorResponse {
        private final String error;
        private final String message;
        private final long timestamp;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }
    }


    // ===============================================================
    // 📝 ENVOYER UN MESSAGE
    // ===============================================================
    @Operation(
            summary = "Send a message",
            description = "Sends a new message from a doctor or a patient to a doctor or a patient"
    )
    @PostMapping
    public ResponseEntity<?> sendMessage(
            @Parameter(description = "Sender Doctor ID, if the sender is a doctor", required = false)
            @RequestParam(required = false) Long senderDoctorId,

            @Parameter(description = "Sender Patient ID, if the sender is a patient", required = false)
            @RequestParam(required = false) Long senderPatientId,

            @Parameter(description = "Receiver Doctor ID, if the receiver is a doctor", required = false)
            @RequestParam(required = false) Long receiverDoctorId,

            @Parameter(description = "Receiver Patient ID, if the receiver is a patient", required = false)
            @RequestParam(required = false) Long receiverPatientId,

            @Parameter(description = "Content of the message", required = true)
            @RequestParam String content
    ) {
        try {
            // Construction du DTO à partir des params
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setSenderDoctorId(senderDoctorId);
            messageDTO.setSenderPatientId(senderPatientId);
            messageDTO.setReceiverDoctorId(receiverDoctorId);
            messageDTO.setReceiverPatientId(receiverPatientId);
            messageDTO.setContent(content);

            // Appel du service pour créer et sauvegarder le message
            MessageDTO savedMessage = messageService.sendMessage(messageDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedMessage);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("VALIDATION_ERROR", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "Error: " + e.getMessage()));
        }
    }
    // ===============================================================
    // Récupérer les conversations pour un médecin
    // ===============================================================
    @Operation(
            summary = "Get doctor conversations",
            description = "Retrieves all conversations of a doctor, each with the last message and its date. " +
                    "Sorted by most recent message first."
    )
    @GetMapping("/doctor/conversations")
    public ResponseEntity<?> getConversationsForDoctor(
            @RequestParam Long doctorId  // ID du médecin connecté
    ) {
        try {
            // Appel du service pour récupérer toutes les conversations
            List<ConversationDTO> conversations = messageService.getConversationsForDoctor(doctorId);
            return ResponseEntity.ok(conversations);

        } catch (IllegalArgumentException e) {
            // Si l'ID du médecin n'existe pas en base
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("NOT_FOUND", e.getMessage()));

        } catch (Exception e) {
            // Pour toute autre erreur inattendue
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "Erreur: " + e.getMessage()));
        }
    }
    // ===============================================================
// Récupérer les conversations pour un patient
// ===============================================================
    @Operation(
            summary = "Get patient conversations",
            description = "Retrieves all conversations of a patient, each with the last message and its date. " +
                    "Sorted by most recent message first."
    )
    @GetMapping("/patient/conversations")
    public ResponseEntity<?> getConversationsForPatient(
            @RequestParam Long patientId  // ID du patient connecté
    ) {
        try {
            // Appel du service pour récupérer toutes les conversations
            List<ConversationDTO> conversations = messageService.getConversationsForPatient(patientId);
            return ResponseEntity.ok(conversations);

        } catch (IllegalArgumentException e) {
            // Si l'ID du patient n'existe pas en base
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("NOT_FOUND", e.getMessage()));

        } catch (Exception e) {
            // Pour toute autre erreur inattendue
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "Erreur: " + e.getMessage()));
        }
    }

}