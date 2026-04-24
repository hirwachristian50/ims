package com.airtel.inventory.service;

import com.airtel.inventory.enums.AssetStatus;
import com.airtel.inventory.model.Asset;
import com.airtel.inventory.model.Assignment;
import com.airtel.inventory.model.AuditLog;
import com.airtel.inventory.model.User;
import com.airtel.inventory.repository.AssetRepository;
import com.airtel.inventory.repository.AssignmentRepository;
import com.airtel.inventory.repository.AuditLogRepository;
import com.airtel.inventory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AssignmentService {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Assignment assignAsset(Long assetId, Long userId, User issuedBy, String purpose, String notes) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new RuntimeException("Asset not found"));
        User assignedTo = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (asset.getStatus() != AssetStatus.AVAILABLE) {
            throw new RuntimeException("Asset is not available for assignment");
        }

        Assignment assignment = new Assignment();
        assignment.setAsset(asset);
        assignment.setAssignedTo(assignedTo);
        assignment.setIssuedBy(issuedBy);
        assignment.setPurpose(purpose);
        assignment.setNotes(notes);
        assignment.setAssignedDate(LocalDateTime.now());
        assignment.setActive(true);
        assignment = assignmentRepository.save(assignment);

        asset.setStatus(AssetStatus.ASSIGNED);
        assetRepository.save(asset);

        AuditLog log = new AuditLog();
        log.setAction("ASSET_ASSIGNED");
        log.setEntityType("Assignment");
        log.setEntityId(assignment.getId());
        log.setDetails(asset.getAssetTag() + " assigned to " + assignedTo.getFullName());
        log.setPerformedBy(issuedBy.getEmail());
        auditLogRepository.save(log);

        return assignment;
    }

    @Transactional
    public void returnAsset(Long assignmentId, User returnedBy) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
        if (!assignment.isActive()) {
            throw new RuntimeException("Asset already returned");
        }
        assignment.setActive(false);
        assignment.setReturnedDate(LocalDateTime.now());
        assignmentRepository.save(assignment);

        Asset asset = assignment.getAsset();
        asset.setStatus(AssetStatus.AVAILABLE);
        assetRepository.save(asset);

        AuditLog log = new AuditLog();
        log.setAction("ASSET_RETURNED");
        log.setEntityType("Assignment");
        log.setEntityId(assignmentId);
        log.setDetails(asset.getAssetTag() + " returned by " + assignment.getAssignedTo().getFullName());
        log.setPerformedBy(returnedBy.getEmail());
        auditLogRepository.save(log);
    }

    public List<Assignment> getActiveAssignments() {
        return assignmentRepository.findByActiveTrue();
    }

    public List<Assignment> getAssignmentHistoryByAsset(Long assetId) {
        return assignmentRepository.findByAssetIdOrderByAssignedDateDesc(assetId);
    }

    public List<Assignment> getActiveAssignmentsByUser(Long userId) {
        return assignmentRepository.findByAssignedToIdAndActiveTrue(userId);
    }

    public Assignment getAssignmentById(Long id) {
        return assignmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Assignment not found"));
    }
}