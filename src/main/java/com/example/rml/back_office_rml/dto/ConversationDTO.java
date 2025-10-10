package com.example.rml.back_office_rml.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
 /**
     * DTO pour représenter une conversation dans la liste.
     * Contient l’interlocuteur, le dernier message et la date.
     */
 @Data
 @NoArgsConstructor
 @AllArgsConstructor
 public class ConversationDTO {

     private Long interlocutorId;        // ID du patient ou médecin
     private String interlocutorName;    // Nom + prénom
     private String lastMessage;         // Contenu du dernier message

     @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
     @JsonProperty(access = JsonProperty.Access.READ_ONLY)
     private LocalDateTime lastMessageDate; // Date du dernier message
 }
