package com.kh.coffeee.feature.branch;

import com.kh.coffeee.feature.branch.dto.BranchRequest;
import com.kh.coffeee.feature.branch.dto.BranchResponse;

import java.util.List;
import java.util.UUID;

public interface BranchService {
    BranchResponse createBranch(BranchRequest request);
    BranchResponse getBranchById(UUID id);
    BranchResponse getBranchByCode(String code);
    List<BranchResponse> getAllBranches();
    BranchResponse updateBranch(UUID id, BranchRequest request);
    void deleteBranch(UUID id);
}