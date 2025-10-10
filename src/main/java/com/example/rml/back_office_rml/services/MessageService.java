package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.ConversationDTO;
import com.example.rml.back_office_rml.dto.MessageDTO;
import com.example.rml.back_office_rml.entities.Doctor;
import com.example.rml.back_office_rml.entities.Patient;

import java.util.List;

/**
 * Interface pour le service de gestion des messages.
 */
public interface MessageService {

    /**
     * Renvoie la liste complète des messages bruts sous forme de MessageDTO.
     * @param doctor le médecin connecté
     * @return liste de MessageDTO
     */
    List<MessageDTO> getMessagesForDoctor(Long doctorId);

    /**
     * Récupère tous les messages d’un patient sous forme de DTO.
     * @param patient le patient connecté
     * @return liste de MessageDTO
     */
    List<MessageDTO> getMessagesForPatient(Long  patientId);

    /**
     * Envoie un message (émetteur ↔ recepteur)
     * @param messageDTO DTO contenant les informations du message
     * @return MessageDTO sauvegardé
     */
    MessageDTO sendMessage(MessageDTO messageDTO);

    /**
     * Transforme les messages d’un médecin en conversations type WhatsApp
     * @param doctor le médecin connecté
     * @return liste de ConversationDTO
     */
    List<ConversationDTO> getConversationsForDoctor(Long doctorId);

    /**
     * Transforme la liste de messages en liste de conversations type WhatsApp, un interlocuteur par ligne avec le dernier message et la date.
     * @param patient le patient connecté
     * @return liste de ConversationDTO
     */
    List<ConversationDTO> getConversationsForPatient(Long patientID);
}
