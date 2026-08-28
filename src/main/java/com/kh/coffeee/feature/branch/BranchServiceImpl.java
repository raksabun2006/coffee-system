package com.kh.coffeee.feature.branch;

import com.kh.coffeee.feature.branch.dto.BranchRequest;
import com.kh.coffeee.feature.branch.dto.BranchResponse;
import com.kh.coffeee.utils.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final BranchMapper branchMapper;

    @Override
    @Transactional
    public BranchResponse createBranch(BranchRequest request) {
        if (branchRepository.existsByCode(request.code())) {
            throw new RuntimeException("Branch code already exists: " + request.code());
        }

        Branch branch = branchMapper.toEntity(request);
        Branch saved = branchRepository.save(branch);
        return branchMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BranchResponse getBranchById(UUID id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found with ID: " + id));
        return branchMapper.toResponse(branch);
    }

    @Override
    @Transactional(readOnly = true)
    public BranchResponse getBranchByCode(String code) {
        Branch branch = branchRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Branch not found with code: " + code));
        return branchMapper.toResponse(branch);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchResponse> getAllBranches() {
        List<Branch> branches = branchRepository.findAll();
        return branchMapper.toResponseList(branches);
    }

    @Override
    @Transactional
    public BranchResponse updateBranch(UUID id, BranchRequest request) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found with ID: " + id));

        if (!branch.getCode().equalsIgnoreCase(request.code()) && branchRepository.existsByCode(request.code())) {
            throw new RuntimeException("Branch code already in use: " + request.code());
        }

        branchMapper.updateEntityFromRequest(branch, request);
        Branch updated = branchRepository.save(branch);
        return branchMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteBranch(UUID id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found with ID: " + id));
        branch.setStatus(Status.INACTIVE);
        branchRepository.save(branch);
    }
}