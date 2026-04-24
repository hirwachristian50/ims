package com.airtel.inventory.controller;

import com.airtel.inventory.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    @GetMapping("/filter")
    public String filterAuditLogs(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String userEmail,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Model model) {

        if (action != null && !action.isEmpty()) {
            model.addAttribute("auditLogs", reportService.getAuditLogsByAction(action));
        } else if (userEmail != null && !userEmail.isEmpty()) {
            model.addAttribute("auditLogs", reportService.getAuditLogsByUser(userEmail));
        } else if (startDate != null && endDate != null && !startDate.isEmpty() && !endDate.isEmpty()) {
            LocalDateTime start = LocalDateTime.parse(startDate + "T00:00:00");
            LocalDateTime end = LocalDateTime.parse(endDate + "T23:59:59");
            model.addAttribute("auditLogs", reportService.getAuditLogsByDateRange(start, end));
        } else {
            model.addAttribute("auditLogs", reportService.getAllAuditLogs());
        }

        model.addAttribute("summary", reportService.getAssetSummary());
        model.addAttribute("assignmentsPerUser", reportService.getAssignmentsPerUser());
        return "reports/index";
    }
}