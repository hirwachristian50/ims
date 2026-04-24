package com.airtel.inventory.service;

import com.airtel.inventory.model.AuditLog;
import com.airtel.inventory.repository.AssetRepository;
import com.airtel.inventory.repository.AssignmentRepository;
import com.airtel.inventory.repository.AuditLogRepository;
import com.airtel.inventory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ReportService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private UserRepository userRepository;

    public List<AuditLog> getAllAuditLogs() {
        try {
            return auditLogRepository.findAllByOrderByTimestampDesc();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public Map<String, Object> getAssetSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalAssets", assetRepository.count());
        summary.put("available", assetRepository.countByStatus(com.airtel.inventory.enums.AssetStatus.AVAILABLE));
        summary.put("assigned", assetRepository.countByStatus(com.airtel.inventory.enums.AssetStatus.ASSIGNED));
        summary.put("underRepair", assetRepository.countByStatus(com.airtel.inventory.enums.AssetStatus.UNDER_REPAIR));
        summary.put("retired", assetRepository.countByStatus(com.airtel.inventory.enums.AssetStatus.RETIRED));
        summary.put("lost", assetRepository.countByStatus(com.airtel.inventory.enums.AssetStatus.LOST));
        summary.put("totalUsers", userRepository.count());
        summary.put("activeAssignments", assignmentRepository.countByActiveTrue());
        return summary;
    }

    public List<Object[]> getAssignmentsPerUser() {
        try {
            return assignmentRepository.countAssignmentsPerUser();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
