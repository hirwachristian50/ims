package com.airtel.inventory.controller;

import com.airtel.inventory.model.Asset;
import com.airtel.inventory.model.Assignment;
import com.airtel.inventory.model.User;
import com.airtel.inventory.repository.AssetRepository;
import com.airtel.inventory.repository.UserRepository;
import com.airtel.inventory.service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/assignments")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String listAssignments(Model model) {
        model.addAttribute("activeAssignments", assignmentService.getActiveAssignments());
        return "assignments/list";
    }

    @GetMapping("/assign")
    public String showAssignForm(@RequestParam(required = false) Long assetId, Model model) {
        model.addAttribute("assets", assetRepository.findByStatus(com.airtel.inventory.enums.AssetStatus.AVAILABLE));
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("preSelectedAssetId", assetId);
        return "assignments/assign-form";
    }

    @PostMapping("/assign")
    public String assignAsset(
            @RequestParam Long assetId,
            @RequestParam Long userId,
            @RequestParam(required = false) String purpose,
            @RequestParam(required = false) String notes,
            @AuthenticationPrincipal UserDetails currentUser,
            RedirectAttributes redirectAttributes) {

        User issuedBy = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            assignmentService.assignAsset(assetId, userId, issuedBy, purpose, notes);
            redirectAttributes.addFlashAttribute("success", "Asset assigned successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/assignments";
    }

    @GetMapping("/return/{id}")
    public String returnAsset(@PathVariable Long id,
                              @AuthenticationPrincipal UserDetails currentUser,
                              RedirectAttributes redirectAttributes) {
        User returnedBy = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        try {
            assignmentService.returnAsset(id, returnedBy);
            redirectAttributes.addFlashAttribute("success", "Asset returned successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/assignments";
    }

    @GetMapping("/history/asset/{assetId}")
    public String assetHistory(@PathVariable Long assetId, Model model) {
        model.addAttribute("asset", assetRepository.findById(assetId).orElse(null));
        model.addAttribute("history", assignmentService.getAssignmentHistoryByAsset(assetId));
        return "assignments/history";
    }
}