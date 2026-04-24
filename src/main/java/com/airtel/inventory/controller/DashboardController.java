package com.airtel.inventory.controller;

import com.airtel.inventory.enums.AssetStatus;
import com.airtel.inventory.repository.AssetRepository;
import com.airtel.inventory.repository.AssignmentRepository;
import com.airtel.inventory.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired private AssetRepository assetRepository;
    @Autowired private AssignmentRepository assignmentRepository;
    @Autowired private AuditLogRepository auditLogRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalAssets", assetRepository.count());
        model.addAttribute("availableAssets", assetRepository.countByStatus(AssetStatus.AVAILABLE));
        model.addAttribute("assignedAssets", assetRepository.countByStatus(AssetStatus.ASSIGNED));
        model.addAttribute("underRepairAssets", assetRepository.countByStatus(AssetStatus.UNDER_REPAIR));
        model.addAttribute("activeAssignments", assignmentRepository.findByActiveTrue());
        model.addAttribute("recentLogs", auditLogRepository.findTop20ByOrderByTimestampDesc());
        return "dashboard/index";
    }
}