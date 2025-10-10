package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.ConversationDTO;
import com.example.rml.back_office_rml.dto.MessageDTO;
import com.example.rml.back_office_rml.entities.Doctor;
import com.example.rml.back_office_rml.entities.Message;
import com.example.rml.back_office_rml.entities.Patient;
import com.example.rml.back_office_rml.repositories.DoctorRepository;
import com.example.rml.back_office_rml.repositories.MessageRepository;
import com.example.rml.back_office_rml.repositories.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public MessageServiceImpl(MessageRepository messageRepository, DoctorRepository doctorRepository, PatientRepository patientRepository) {
        this.messageRepository = messageRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    /**
     * Convertit une entité Message en MessageDTO.
     */
    private MessageDTO toDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setContent(message.getContent());
        dto.setCreatedAt(message.getCreatedAt());

        if (message.getSenderDoctor() != null) {
            dto.setSenderDoctorId(message.getSenderDoctor().getDoctorId());
            dto.setSenderDoctorFirstName(message.getSenderDoctor().getFirstName() + " " + message.getSenderDoctor().getLastName());
        }
        if (message.getSenderPatient() != null) {
            dto.setSenderPatientId(message.getSenderPatient().getPatientId());
            dto.setSenderPatientFirstName(message.getSenderPatient().getFirstName() + " " + message.getSenderPatient().getLastName());
        }
        if (message.getReceiverDoctor() != null) {
            dto.setReceiverDoctorId(message.getReceiverDoctor().getDoctorId());
            dto.setReceiverDoctorFirstName(message.getReceiverDoctor().getFirstName() + " " + message.getReceiverDoctor().getLastName());
        }
        if (message.getReceiverPatient() != null) {
            dto.setReceiverPatientId(message.getReceiverPatient().getPatientId());
            dto.setReceiverPatientFirstName(message.getReceiverPatient().getFirstName() + " " + message.getReceiverPatient().getLastName());
        }

        return dto;
    }

    /**
     * Validation complète du message avant enregistrement.
     */
    private void validateMessageDTO(MessageDTO messageDTO) {
        // [1] Vérification du contenu
        if (messageDTO == null || messageDTO.getContent() == null || messageDTO.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Le contenu du message est requis.");
        }

        // [2] Extraction des identifiants
        Long senderDoctorId = messageDTO.getSenderDoctorId();
        Long senderPatientId = messageDTO.getSenderPatientId();
        Long receiverDoctorId = messageDTO.getReceiverDoctorId();
        Long receiverPatientId = messageDTO.getReceiverPatientId();

        // [3] Validation de l’expéditeur
        if (senderDoctorId == null && senderPatientId == null) {
            throw new IllegalArgumentException("Un expéditeur (senderDoctorId ou senderPatientId) est requis.");
        }
        if (senderDoctorId != null && senderPatientId != null) {
            throw new IllegalArgumentException("Renseignez un seul expéditeur (médecin OU patient).");
        }

        // [4] Validation du destinataire
        if (receiverDoctorId == null && receiverPatientId == null) {
            throw new IllegalArgumentException("Un destinataire (receiverDoctorId ou receiverPatientId) est requis.");
        }
        if (receiverDoctorId != null && receiverPatientId != null) {
            throw new IllegalArgumentException("Renseignez un seul destinataire (médecin OU patient).");
        }
    }

    /**
     * Envoie un message : validation, récupération des entités, sauvegarde et renvoi du DTO.
     */
    @Override
    public MessageDTO sendMessage(MessageDTO messageDTO) {

        // Validation centralisée
        validateMessageDTO(messageDTO);

        Long senderDoctorId = messageDTO.getSenderDoctorId();
        Long senderPatientId = messageDTO.getSenderPatientId();
        Long receiverDoctorId = messageDTO.getReceiverDoctorId();
        Long receiverPatientId = messageDTO.getReceiverPatientId();

        Doctor senderDoctor = null, receiverDoctor = null;
        Patient senderPatient = null, receiverPatient = null;

        // Chargement des entités
        if (senderDoctorId != null) {
            senderDoctor = doctorRepository.findById(senderDoctorId)
                    .orElseThrow(() -> new IllegalArgumentException("Médecin expéditeur introuvable (id=" + senderDoctorId + ")"));
        } else {
            senderPatient = patientRepository.findById(senderPatientId)
                    .orElseThrow(() -> new IllegalArgumentException("Patient expéditeur introuvable (id=" + senderPatientId + ")"));
        }

        if (receiverDoctorId != null) {
            receiverDoctor = doctorRepository.findById(receiverDoctorId)
                    .orElseThrow(() -> new IllegalArgumentException("Médecin destinataire introuvable (id=" + receiverDoctorId + ")"));
        } else {
            receiverPatient = patientRepository.findById(receiverPatientId)
                    .orElseThrow(() -> new IllegalArgumentException("Patient destinataire introuvable (id=" + receiverPatientId + ")"));
        }

        // Règles métier
        if (senderPatientId != null && receiverPatientId != null) {
            throw new IllegalArgumentException("Les échanges patient → patient ne sont pas supportés.");
        }
        if (senderDoctorId != null && receiverDoctorId != null && senderDoctorId.equals(receiverDoctorId)) {
            throw new IllegalArgumentException("Impossible d'envoyer un message à soi-même (médecin).");
        }
        if (senderPatientId != null && receiverPatientId != null && senderPatientId.equals(receiverPatientId)) {
            throw new IllegalArgumentException("Impossible d'envoyer un message à soi-même (patient).");
        }

        // Création du message
        Message message = new Message();
        message.setContent(messageDTO.getContent().trim());
        message.setSenderDoctor(senderDoctor);
        message.setSenderPatient(senderPatient);
        message.setReceiverDoctor(receiverDoctor);
        message.setReceiverPatient(receiverPatient);

        // Sauvegarde
        Message savedMessage = messageRepository.save(message);

        // Conversion en DTO
        return toDTO(savedMessage);
    }

    /**
     * Liste des messages liés à un médecin (envoyés ou reçus)
     */
    @Override
    public List<MessageDTO> getMessagesForDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Médecin introuvable (id=" + doctorId + ")"));

        return messageRepository.findAllByDoctor(doctor)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Liste des messages liés à un patient (envoyés ou reçus)
     */
    @Override
    public List<MessageDTO> getMessagesForPatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient introuvable (id=" + patientId + ")"));

        return messageRepository.findAllByPatient(patient)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Liste des conversations d’un patient (regroupées par interlocuteur)
     *
     * Cette méthode reconstruit les conversations du patient connecté.
     * Chaque conversation correspond à un interlocuteur unique (ici, un médecin).
     * On récupère pour chacun le dernier message échangé, trié par date décroissante.
     */
    @Override
    public List<ConversationDTO> getConversationsForPatient(Long patientId) {

        /**
         * [1] Récupération du patient à partir de son ID.
         * Si l’ID n’existe pas, on lève une exception claire.
         */
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient introuvable (id=" + patientId + ")"));

        /**
         * [2] Récupération de tous les messages associés à ce patient :
         *    - messages envoyés par le patient
         *    - messages reçus par le patient
         */
        List<Message> messages = messageRepository.findAllByPatient(patient);

        /**
         * [3] Préparation d'une Map pour regrouper les conversations par interlocuteur.
         *    Clé   → ID du médecin interlocuteur
         *    Valeur → ConversationDTO contenant les infos du dernier message
         */
        Map<Long, ConversationDTO> conversationsMap = new HashMap<>();

        // [4] Parcours de chaque message lié à ce patient
        for (Message m : messages) {
            Long interlocutorId;
            String interlocutorName;

            /**
             * [4.1] Détermination de l’interlocuteur :
             * Le patient ne parle qu’avec des médecins,
             * donc on regarde si le message vient d’un médecin (senderDoctor)
             * ou s’il lui est adressé (receiverDoctor).
             */
            if (m.getSenderDoctor() != null) {
                interlocutorId = m.getSenderDoctor().getDoctorId();
                interlocutorName = m.getSenderDoctor().getFirstName() + " " + m.getSenderDoctor().getLastName();
            } else if (m.getReceiverDoctor() != null) {
                interlocutorId = m.getReceiverDoctor().getDoctorId();
                interlocutorName = m.getReceiverDoctor().getFirstName() + " " + m.getReceiverDoctor().getLastName();
            } else {
                // Sécurité : message sans médecin associé → on ignore
                continue;
            }

            /**
             * [4.2] Vérification si une conversation existe déjà dans la Map
             * pour ce médecin interlocuteur.
             */
            ConversationDTO existing = conversationsMap.get(interlocutorId);

            /**
             * [4.3] Si la conversation n'existe pas encore OU
             * si ce message est plus récent que le dernier enregistré,
             * on met à jour la Map avec ce message comme "dernier échange".
             */
            if (existing == null || m.getCreatedAt().isAfter(existing.getLastMessageDate())) {
                conversationsMap.put(interlocutorId, new ConversationDTO(
                        interlocutorId,
                        interlocutorName,
                        m.getContent(),
                        m.getCreatedAt()
                ));
            }
        }

        /**
         * [5] Transformation de la Map en une liste triée :
         * - Tri décroissant selon la date du dernier message
         *   (les conversations récentes apparaissent en premier)
         */
        return conversationsMap.values().stream()
                .sorted((c1, c2) -> c2.getLastMessageDate().compareTo(c1.getLastMessageDate()))
                .collect(Collectors.toList());
    }

    /**
     * Liste des conversations d’un médecin (déjà implémentée)
     * → Logique identique mais adaptée pour le médecin connecté
     * qui peut échanger avec d’autres médecins ou des patients.
     */
    @Override
    public List<ConversationDTO> getConversationsForDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Médecin introuvable (id=" + doctorId + ")"));

        List<Message> messages = messageRepository.findAllByDoctor(doctor);
        Map<Long, ConversationDTO> conversationsMap = new HashMap<>();

        for (Message m : messages) {
            Long interlocutorId;
            String interlocutorName;

            // Détermination de l’interlocuteur (autre médecin ou patient)
            if (m.getSenderDoctor() != null && !m.getSenderDoctor().getDoctorId().equals(doctorId)) {
                interlocutorId = m.getSenderDoctor().getDoctorId();
                interlocutorName = m.getSenderDoctor().getFirstName() + " " + m.getSenderDoctor().getLastName();
            } else if (m.getReceiverDoctor() != null && !m.getReceiverDoctor().getDoctorId().equals(doctorId)) {
                interlocutorId = m.getReceiverDoctor().getDoctorId();
                interlocutorName = m.getReceiverDoctor().getFirstName() + " " + m.getReceiverDoctor().getLastName();
            } else if (m.getSenderPatient() != null) {
                interlocutorId = m.getSenderPatient().getPatientId();
                interlocutorName = m.getSenderPatient().getFirstName() + " " + m.getSenderPatient().getLastName();
            } else if (m.getReceiverPatient() != null) {
                interlocutorId = m.getReceiverPatient().getPatientId();
                interlocutorName = m.getReceiverPatient().getFirstName() + " " + m.getReceiverPatient().getLastName();
            } else {
                continue;
            }

            ConversationDTO existing = conversationsMap.get(interlocutorId);
            if (existing == null || m.getCreatedAt().isAfter(existing.getLastMessageDate())) {
                conversationsMap.put(interlocutorId, new ConversationDTO(
                        interlocutorId,
                        interlocutorName,
                        m.getContent(),
                        m.getCreatedAt()
                ));
            }
        }

        return conversationsMap.values().stream()
                .sorted((c1, c2) -> c2.getLastMessageDate().compareTo(c1.getLastMessageDate()))
                .collect(Collectors.toList());
    }
}
