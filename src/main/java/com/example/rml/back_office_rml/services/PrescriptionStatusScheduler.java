package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.entities.Prescription;
import com.example.rml.back_office_rml.enums.PrescriptionStatus;
import com.example.rml.back_office_rml.repositories.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrescriptionStatusScheduler {

    private final PrescriptionRepository prescriptionRepository;

    // Vérifie chaque jour à minuit les ordonnances expirées et met leur statut à EXPIRED
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void updateExpiredPrescriptions() {
        List<Prescription> expiredPrescriptions = prescriptionRepository.findExpiredPrescriptions(LocalDate.now());
        for (Prescription prescription : expiredPrescriptions) {
            prescription.setStatus(PrescriptionStatus.EXPIRED);
        }
    }
}
