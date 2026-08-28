package com.kh.coffeee.feature.report;

import com.kh.coffeee.feature.report.dto.DailyBranchReportResponse;

import java.time.LocalDate;
import java.util.UUID;

public interface ReportService {
    DailyBranchReportResponse getDailyBranchReport(UUID branchId, LocalDate date);
}