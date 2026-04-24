package com.airtel.inventory.controller;

import com.airtel.inventory.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping
    public String reportsDashboard(Model model) {
        model.addAttribute("auditLogs", reportService.getAllAuditLogs());
        model.addAttribute("summary", reportService.getAssetSummary());
        model.addAttribute("assignmentsPerUser", reportService.getAssignmentsPerUser());
        return "reports/index";
    }
}
