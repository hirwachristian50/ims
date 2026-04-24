package com.airtel.inventory.controller;

import com.airtel.inventory.model.Department;
import com.airtel.inventory.service.DepartmentService;
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
@RequestMapping("/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping
    public String listDepartments(Model model) {
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "departments/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("department", new Department());
        return "departments/form";
    }

    @PostMapping("/add")
    public String addDepartment(
            @Valid @ModelAttribute("department") Department department,
            BindingResult result,
            @AuthenticationPrincipal UserDetails currentUser,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "departments/form";
        }

        try {
            departmentService.createDepartment(department, currentUser.getUsername());
            redirectAttributes.addFlashAttribute("success", "Department created successfully!");
            return "redirect:/departments";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/departments/add";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Department department = departmentService.getDepartmentById(id);
        model.addAttribute("department", department);
        return "departments/form";
    }

    @PostMapping("/edit/{id}")
    public String updateDepartment(
            @PathVariable Long id,
            @Valid @ModelAttribute("department") Department department,
            BindingResult result,
            @AuthenticationPrincipal UserDetails currentUser,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "departments/form";
        }

        department.setId(id);
        try {
            departmentService.updateDepartment(department, currentUser.getUsername());
            redirectAttributes.addFlashAttribute("success", "Department updated!");
            return "redirect:/departments";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/departments/edit/" + id;
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteDepartment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser,
            RedirectAttributes redirectAttributes) {

        try {
            departmentService.deleteDepartment(id, currentUser.getUsername());
            redirectAttributes.addFlashAttribute("success", "Department deleted!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/departments";
    }

    @GetMapping("/view/{id}")
    public String viewDepartment(@PathVariable Long id, Model model) {
        model.addAttribute("department", departmentService.getDepartmentById(id));
        return "departments/detail";
    }
}