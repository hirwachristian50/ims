package com.airtel.inventory.service;

import com.airtel.inventory.enums.Role;
import com.airtel.inventory.model.User;
import com.airtel.inventory.model.AuditLog;
import com.airtel.inventory.repository.UserRepository;
import com.airtel.inventory.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User createUser(User user, String performedBy) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved = userRepository.save(user);

        AuditLog log = new AuditLog();
        log.setAction("USER_CREATED");
        log.setEntityType("User");
        log.setEntityId(saved.getId());
        log.setDetails("User created: " + saved.getFullName() + " (" + saved.getEmail() + ")");
        log.setPerformedBy(performedBy);
        auditLogRepository.save(log);
        return saved;
    }

    public User updateUser(User user, String performedBy) {
        User existing = getUserById(user.getId());
        // Preserve password and enabled status from existing record
        user.setPassword(existing.getPassword());
        user.setEnabled(existing.isEnabled());
        User updated = userRepository.save(user);

        AuditLog log = new AuditLog();
        log.setAction("USER_UPDATED");
        log.setEntityType("User");
        log.setEntityId(updated.getId());
        log.setDetails("User updated: " + updated.getFullName());
        log.setPerformedBy(performedBy);
        auditLogRepository.save(log);
        return updated;
    }

    public void deleteUser(Long id, String performedBy) {
        User user = getUserById(id);
        userRepository.deleteById(id);
        AuditLog log = new AuditLog();
        log.setAction("USER_DELETED");
        log.setEntityType("User");
        log.setEntityId(id);
        log.setDetails("User deleted: " + user.getFullName() + " (" + user.getEmail() + ")");
        log.setPerformedBy(performedBy);
        auditLogRepository.save(log);
    }

    public void changePassword(Long userId, String newPassword, String performedBy) {
        User user = getUserById(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        AuditLog log = new AuditLog();
        log.setAction("PASSWORD_CHANGED");
        log.setEntityType("User");
        log.setEntityId(userId);
        log.setDetails("Password changed for user: " + user.getFullName());
        log.setPerformedBy(performedBy);
        auditLogRepository.save(log);
    }

    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public List<User> getUsersByDepartment(Long departmentId) {
        return userRepository.findByDepartmentId(departmentId);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}