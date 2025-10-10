package com.example.rml.back_office_rml.entities;

import com.example.rml.back_office_rml.enums.MedicalSpecialty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long doctorId; // ID du médecin

    @OneToOne
    @JoinColumn(name = "id_utilisateur", nullable = false , referencedColumnName = "id_utilisateur")
    private Users user; // lien avec l'utilisateur

    @Column(nullable = false)
    private String lastName; // nom

    @Column(nullable = false)
    private String firstName; // prénom

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MedicalSpecialty specialty; // spécialité médicale

    @Column(nullable = false)
    private String phone; // téléphone

    @Lob
    @Column(name = "photo", columnDefinition = "LONGBLOB")
    private byte[] photo; // photo du médecin

    @Lob
    @Column(name = "justificatifs", columnDefinition = "LONGBLOB")
    private byte[] documents; // documents justificatifs

    @Entity
    @Table(name = "messages")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {

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

        @PrePersist
        protected void onCreate() {
            createdAt = LocalDateTime.now();
        }


    }
}
