package com.kh.coffeee.feature.shift;

import com.kh.coffeee.feature.auth.User;
import com.kh.coffeee.feature.auth.UserRepository;
import com.kh.coffeee.feature.branch.Branch;
import com.kh.coffeee.feature.branch.BranchRepository;
import com.kh.coffeee.feature.shift.dto.CloseShiftRequest;
import com.kh.coffeee.feature.shift.dto.OpenShiftRequest;
import com.kh.coffeee.feature.shift.dto.ShiftResponse;
import com.kh.coffeee.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShiftServiceImpl implements ShiftService {

    private final ShiftRepository shiftRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final ShiftMapper shiftMapper;

    @Override
    @Transactional
    public ShiftResponse openShift(OpenShiftRequest request) {
        User cashier = getCurrentAuthenticatedUser();

        if (shiftRepository.findOpenShiftByCashierId(cashier.getId()).isPresent()) {
            throw new IllegalStateException("You already have an active open shift. Please close it first.");
        }

        Branch branch = branchRepository.findById(request.branchId())
                .orElseThrow(() -> new RuntimeException("Branch not found with ID: " + request.branchId()));

        Shift shift = Shift.builder()
                .branch(branch)
                .cashier(cashier)
                .startingCash(request.startingCash())
                .cashSales(BigDecimal.ZERO)
                .status(ShiftStatus.OPEN)
                .openedAt(OffsetDateTime.now())
                .notes(request.notes())
                .build();

        Shift saved = shiftRepository.save(shift);
        return shiftMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ShiftResponse closeShift(UUID shiftId, CloseShiftRequest request) {
        Shift shift = shiftRepository.findByIdWithDetails(shiftId)
                .orElseThrow(() -> new RuntimeException("Shift not found with ID: " + shiftId));

        if (shift.getStatus() == ShiftStatus.CLOSED) {
            throw new IllegalStateException("This shift is already closed.");
        }

        OffsetDateTime now = OffsetDateTime.now();
        BigDecimal cashSales = shiftRepository.calculateCashSalesForShift(
                shift.getCashier().getId(),
                shift.getBranch().getId(),
                shift.getOpenedAt(),
                now
        );

        BigDecimal expectedCash = shift.getStartingCash().add(cashSales != null ? cashSales : BigDecimal.ZERO);
        BigDecimal discrepancy = request.actualCashCounted().subtract(expectedCash);

        shift.setCashSales(cashSales != null ? cashSales : BigDecimal.ZERO);
        shift.setExpectedCash(expectedCash);
        shift.setActualCashCounted(request.actualCashCounted());
        shift.setDiscrepancy(discrepancy);
        shift.setStatus(ShiftStatus.CLOSED);
        shift.setClosedAt(now);

        if (request.notes() != null && !request.notes().isBlank()) {
            shift.setNotes((shift.getNotes() != null ? shift.getNotes() + " | Close Notes: " : "") + request.notes());
        }

        Shift saved = shiftRepository.save(shift);
        return shiftMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ShiftResponse getCurrentActiveShift() {
        User cashier = getCurrentAuthenticatedUser();
        Shift shift = shiftRepository.findOpenShiftByCashierId(cashier.getId())
                .orElseThrow(() -> new RuntimeException("No active open shift found for the current user."));
        return shiftMapper.toResponse(shift);
    }

    @Override
    @Transactional(readOnly = true)
    public ShiftResponse getShiftById(UUID id) {
        Shift shift = shiftRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Shift not found with ID: " + id));
        return shiftMapper.toResponse(shift);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShiftResponse> getShiftsByBranch(UUID branchId) {
        List<Shift> shifts = shiftRepository.findAllByBranchIdOrderByOpenedAtDesc(branchId);
        return shiftMapper.toResponseList(shifts);
    }

    private User getCurrentAuthenticatedUser() {
        String username = SecurityUtils.getCurrentUsername()
                .orElseThrow(() -> new RuntimeException("Unauthenticated user context"));
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User account not found for username: " + username));
    }
}