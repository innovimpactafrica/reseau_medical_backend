package com.example.rml.back_office_rml.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // texte du message

    @ManyToOne
    @JoinColumn(name = "sender_doctor_id")
    private Doctor senderDoctor; // facultatif, rempli si le message vient d'un médecin

    @ManyToOne
    @JoinColumn(name = "sender_patient_id")
    private Patient senderPatient; // facultatif, rempli si le message vient d'un patient

    @ManyToOne
    @JoinColumn(name = "receiver_doctor_id")
    private Doctor receiverDoctor; // le médecin destinataire

    @ManyToOne
    @JoinColumn(name = "receiver_patient_id")
    private Patient receiverPatient; // le patient destinataire

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}