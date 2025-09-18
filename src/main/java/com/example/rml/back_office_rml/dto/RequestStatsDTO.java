package com.example.rml.back_office_rml.dto;

import lombok.Data;

@Data
public class RequestStatsDTO {
    // Totaux globaux
    private long totalRequests;
    private long totalPending;
    private long totalApproved;
    private long totalRefused;

    // Totaux pour les médecins
    private long totalDoctorRequests;
    private long totalDoctorPending;
    private long totalDoctorApproved;
    private long totalDoctorRefused;

    // Totaux pour les centres de santé
    private long totalHealthCenterRequests;
    private long totalHealthCenterPending;
    private long totalHealthCenterApproved;
    private long totalHealthCenterRefused;
}

