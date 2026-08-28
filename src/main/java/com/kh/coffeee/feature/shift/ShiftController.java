package com.kh.coffeee.feature.shift;

import com.kh.coffeee.feature.shift.dto.CloseShiftRequest;
import com.kh.coffeee.feature.shift.dto.OpenShiftRequest;
import com.kh.coffeee.feature.shift.dto.ShiftResponse;
import com.kh.coffeee.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shifts")
@RequiredArgsConstructor
public class ShiftController {

    private final ShiftService shiftService;

    @PostMapping("/open")
    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER', 'MANAGER')")
    public ResponseEntity<ApiResponse<ShiftResponse>> openShift(@Valid @RequestBody OpenShiftRequest request) {
        ShiftResponse response = shiftService.openShift(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Shift opened successfully", response));
    }

    @PostMapping("/{id}/close")
    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER', 'MANAGER')")
    public ResponseEntity<ApiResponse<ShiftResponse>> closeShift(
            @PathVariable UUID id,
            @Valid @RequestBody CloseShiftRequest request
    ) {
        ShiftResponse response = shiftService.closeShift(id, request);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Shift closed and reconciled successfully", response));
    }

    @GetMapping("/current")
    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER', 'MANAGER')")
    public ResponseEntity<ApiResponse<ShiftResponse>> getCurrentActiveShift() {
        ShiftResponse response = shiftService.getCurrentActiveShift();
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Active shift retrieved successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<ShiftResponse>> getShiftById(@PathVariable UUID id) {
        ShiftResponse response = shiftService.getShiftById(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Shift details retrieved successfully", response));
    }

    @GetMapping("/branch/{branchId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<ShiftResponse>>> getShiftsByBranch(@PathVariable UUID branchId) {
        List<ShiftResponse> responses = shiftService.getShiftsByBranch(branchId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Branch shifts retrieved successfully", responses));
    }
}