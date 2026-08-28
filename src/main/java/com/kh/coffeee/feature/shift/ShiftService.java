package com.kh.coffeee.feature.shift;

import com.kh.coffeee.feature.shift.dto.CloseShiftRequest;
import com.kh.coffeee.feature.shift.dto.OpenShiftRequest;
import com.kh.coffeee.feature.shift.dto.ShiftResponse;

import java.util.List;
import java.util.UUID;

public interface ShiftService {
    ShiftResponse openShift(OpenShiftRequest request);
    ShiftResponse closeShift(UUID shiftId, CloseShiftRequest request);
    ShiftResponse getCurrentActiveShift();
    ShiftResponse getShiftById(UUID id);
    List<ShiftResponse> getShiftsByBranch(UUID branchId);
}