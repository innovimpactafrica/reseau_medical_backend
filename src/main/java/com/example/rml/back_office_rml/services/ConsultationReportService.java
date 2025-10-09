package com.example.rml.back_office_rml.services;

import com.example.rml.back_office_rml.dto.ConsultationReportDTO;
import java.util.List;

public interface ConsultationReportService {

    ConsultationReportDTO createReport(ConsultationReportDTO dto);

    ConsultationReportDTO updateReport(Long reportId, ConsultationReportDTO dto);

    List<ConsultationReportDTO> getAllReports();

    List<ConsultationReportDTO> getReportsByRecordId(Long recordId);


}