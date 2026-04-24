package com.airtel.inventory.config;

import com.airtel.inventory.enums.Role;
import com.airtel.inventory.model.Department;
import com.airtel.inventory.model.User;
import com.airtel.inventory.repository.DepartmentRepository;
import com.airtel.inventory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private DepartmentRepository departmentRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (departmentRepository.count() == 0) {
            Department it = new Department();
            it.setName("IT Department");
            it.setDescription("Information Technology");
            it.setLocation("Head Office");
            departmentRepository.save(it);
            
            Department hr = new Department();
            hr.setName("Human Resources");
            hr.setDescription("HR Department");
            hr.setLocation("Head Office");
            departmentRepository.save(hr);
            
            Department finance = new Department();
            finance.setName("Finance");
            finance.setDescription("Finance Department");
            finance.setLocation("Head Office");
            departmentRepository.save(finance);
            System.out.println("✅ Created 3 departments.");
        }
        
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setFullName("System Administrator");
            admin.setEmail("24RP00849");
            admin.setPassword(passwordEncoder.encode("24RP01801"));
            admin.setRole(Role.ROLE_ADMIN);
            admin.setPhone("+250780000000");
            admin.setEnabled(true);
            Department itDept = departmentRepository.findByName("IT Department").orElse(null);
            admin.setDepartment(itDept);
            userRepository.save(admin);
            System.out.println("✅ SysAdmin user created.");
            System.out.println("   Username: 24RP00849");
            System.out.println("   Password: 24RP01801");
        }
    }
}