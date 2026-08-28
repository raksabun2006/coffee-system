package com.kh.coffeee.feature.report;

import com.kh.coffeee.feature.report.dto.DailyBranchReportResponse;
import com.kh.coffeee.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/daily/branch/{branchId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<DailyBranchReportResponse>> getDailyBranchReport(
            @PathVariable UUID branchId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        DailyBranchReportResponse report = reportService.getDailyBranchReport(branchId, date);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Daily branch report generated successfully", report));
    }
}