package com.example.rml.back_office_rml.entities;

import com.example.rml.back_office_rml.enums.SlotStatus;
import com.example.rml.back_office_rml.repositories.SlotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SlotScheduler {

    private final SlotRepository slotRepository;

    /**
     * Tâche planifiée : exécute tous les jours à minuit
     * Vérifie les slots passés et les marque comme "EXPIRED"
     */
    @Scheduled(cron = "0 0 0 * * *") // Tous les jours à 00:00
    public void markExpiredSlots() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        List<Slot> expiredSlots = slotRepository.findAll().stream()
                .filter(slot -> slot.getSlotDate().isBefore(today)
                        || (slot.getSlotDate().isEqual(today) && slot.getEndTime().isBefore(now)))
                .filter(slot -> slot.getStatus() == SlotStatus.AVAILABLE || slot.getStatus() == SlotStatus.RESERVED)
                .toList();

        expiredSlots.forEach(slot -> slot.setStatus(SlotStatus.EXPIRED));

        slotRepository.saveAll(expiredSlots);
        log.info(" {} slots expirés ont été mis à jour automatiquement.", expiredSlots.size());
    }
}
