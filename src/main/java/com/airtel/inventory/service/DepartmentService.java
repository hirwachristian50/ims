package com.airtel.inventory.service;

import com.airtel.inventory.model.Department;
import com.airtel.inventory.model.AuditLog;
import com.airtel.inventory.repository.DepartmentRepository;
import com.airtel.inventory.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Department not found"));
    }

    public Department createDepartment(Department department, String performedBy) {
        if (departmentRepository.existsByName(department.getName())) {
            throw new RuntimeException("Department name already exists: " + department.getName());
        }
        Department saved = departmentRepository.save(department);

        AuditLog log = new AuditLog();
        log.setAction("DEPARTMENT_CREATED");
        log.setEntityType("Department");
        log.setEntityId(saved.getId());
        log.setDetails("Department created: " + saved.getName());
        log.setPerformedBy(performedBy);
        auditLogRepository.save(log);

        return saved;
    }

    public Department updateDepartment(Department department, String performedBy) {
        Department existing = getDepartmentById(department.getId());
        if (!existing.getName().equals(department.getName()) && departmentRepository.existsByName(department.getName())) {
            throw new RuntimeException("Department name already exists: " + department.getName());
        }
        Department updated = departmentRepository.save(department);

        AuditLog log = new AuditLog();
        log.setAction("DEPARTMENT_UPDATED");
        log.setEntityType("Department");
        log.setEntityId(updated.getId());
        log.setDetails("Department updated: " + updated.getName());
        log.setPerformedBy(performedBy);
        auditLogRepository.save(log);

        return updated;
    }

    public void deleteDepartment(Long id, String performedBy) {
        Department dept = getDepartmentById(id);
        departmentRepository.deleteById(id);

        AuditLog log = new AuditLog();
        log.setAction("DEPARTMENT_DELETED");
        log.setEntityType("Department");
        log.setEntityId(id);
        log.setDetails("Department deleted: " + dept.getName());
        log.setPerformedBy(performedBy);
        auditLogRepository.save(log);
    }
}