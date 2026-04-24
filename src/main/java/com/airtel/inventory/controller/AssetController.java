package com.airtel.inventory.controller;

import com.airtel.inventory.enums.AssetStatus;
import com.airtel.inventory.enums.DeviceCondition;
import com.airtel.inventory.enums.DeviceType;
import com.airtel.inventory.model.Asset;
import com.airtel.inventory.model.Department;
import com.airtel.inventory.repository.DepartmentRepository;
import com.airtel.inventory.service.AssetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/assets")
public class AssetController {

    @Autowired
    private AssetService assetService;
    
    @Autowired
    private DepartmentRepository departmentRepository;

    @GetMapping
    public String listAssets(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) DeviceType type,
            @RequestParam(required = false) AssetStatus status,
            Model model) {
        
        if (search != null && !search.isBlank()) {
            model.addAttribute("assets", assetService.searchAssets(search));
            model.addAttribute("searchKeyword", search);
        } else if (type != null) {
            model.addAttribute("assets", assetService.getAssetsByDeviceType(type));
            model.addAttribute("selectedType", type);
        } else if (status != null) {
            model.addAttribute("assets", assetService.getAssetsByStatus(status));
            model.addAttribute("selectedStatus", status);
        } else {
            model.addAttribute("assets", assetService.getAllAssets());
        }
        
        model.addAttribute("deviceTypes", DeviceType.values());
        model.addAttribute("assetStatuses", AssetStatus.values());
        model.addAttribute("conditions", DeviceCondition.values());
        model.addAttribute("departments", departmentRepository.findAll());
        
        return "assets/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("asset", new Asset());
        model.addAttribute("deviceTypes", DeviceType.values());
        model.addAttribute("conditions", DeviceCondition.values());
        model.addAttribute("assetStatuses", AssetStatus.values());
        model.addAttribute("departments", departmentRepository.findAll());
        return "assets/form";
    }

    @PostMapping("/add")
    public String addAsset(
            @Valid @ModelAttribute("asset") Asset asset,
            BindingResult result,
            @AuthenticationPrincipal UserDetails currentUser,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        if (result.hasErrors()) {
            model.addAttribute("deviceTypes", DeviceType.values());
            model.addAttribute("conditions", DeviceCondition.values());
            model.addAttribute("departments", departmentRepository.findAll());
            return "assets/form";
        }
        
        try {
            assetService.saveAsset(asset, currentUser.getUsername());
            redirectAttributes.addFlashAttribute("success", "Asset registered successfully!");
            return "redirect:/assets";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/assets/add";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Asset asset = assetService.getAssetById(id);
        model.addAttribute("asset", asset);
        model.addAttribute("deviceTypes", DeviceType.values());
        model.addAttribute("conditions", DeviceCondition.values());
        model.addAttribute("departments", departmentRepository.findAll());
        return "assets/form";
    }

    @PostMapping("/edit/{id}")
    public String updateAsset(
            @PathVariable Long id,
            @Valid @ModelAttribute("asset") Asset asset,
            BindingResult result,
            @AuthenticationPrincipal UserDetails currentUser,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        if (result.hasErrors()) {
            model.addAttribute("deviceTypes", DeviceType.values());
            model.addAttribute("conditions", DeviceCondition.values());
            model.addAttribute("departments", departmentRepository.findAll());
            return "assets/form";
        }
        
        asset.setId(id);
        assetService.updateAsset(asset, currentUser.getUsername());
        redirectAttributes.addFlashAttribute("success", "Asset updated!");
        return "redirect:/assets";
    }

    @GetMapping("/delete/{id}")
    public String deleteAsset(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser,
            RedirectAttributes redirectAttributes) {
        
        try {
            assetService.deleteAsset(id, currentUser.getUsername());
            redirectAttributes.addFlashAttribute("success", "Asset deleted!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete – asset may be assigned.");
        }
        return "redirect:/assets";
    }
    
    @GetMapping("/view/{id}")
    public String viewAsset(@PathVariable Long id, Model model) {
        model.addAttribute("asset", assetService.getAssetById(id));
        return "assets/detail";
    }
}