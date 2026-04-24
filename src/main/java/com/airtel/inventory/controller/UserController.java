package com.airtel.inventory.controller;

import com.airtel.inventory.enums.Role;
import com.airtel.inventory.model.User;
import com.airtel.inventory.repository.DepartmentRepository;
import com.airtel.inventory.service.UserService;
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
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("roles", Role.values());
        model.addAttribute("departments", departmentRepository.findAll());
        return "users/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", Role.values());
        model.addAttribute("departments", departmentRepository.findAll());
        return "users/form";
    }

    @PostMapping("/add")
    public String addUser(
            @Valid @ModelAttribute("user") User user,
            BindingResult result,
            @AuthenticationPrincipal UserDetails currentUser,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("roles", Role.values());
            model.addAttribute("departments", departmentRepository.findAll());
            return "users/form";
        }

        try {
            userService.createUser(user, currentUser.getUsername());
            redirectAttributes.addFlashAttribute("success", "User created successfully!");
            return "redirect:/users";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/users/add";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        model.addAttribute("departments", departmentRepository.findAll());
        return "users/form";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(
            @PathVariable Long id,
            @Valid @ModelAttribute("user") User user,
            BindingResult result,
            @AuthenticationPrincipal UserDetails currentUser,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("roles", Role.values());
            model.addAttribute("departments", departmentRepository.findAll());
            return "users/form";
        }

        user.setId(id);
        userService.updateUser(user, currentUser.getUsername());
        redirectAttributes.addFlashAttribute("success", "User updated!");
        return "redirect:/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser,
            RedirectAttributes redirectAttributes) {

        try {
            userService.deleteUser(id, currentUser.getUsername());
            redirectAttributes.addFlashAttribute("success", "User deleted!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/view/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.getUserById(id));
        return "users/detail";
    }
}