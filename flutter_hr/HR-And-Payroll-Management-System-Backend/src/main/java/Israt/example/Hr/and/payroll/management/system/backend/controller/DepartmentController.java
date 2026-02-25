package Israt.example.Hr.and.payroll.management.system.backend.controller;

import Israt.example.Hr.and.payroll.management.system.backend.entity.Department;
import Israt.example.Hr.and.payroll.management.system.backend.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/departments")
@CrossOrigin(origins = "*")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> createDepartment(@RequestBody Department department) {
        try {
            Department newDepartment = departmentService.createDepartment(department);
            return ResponseEntity.status(HttpStatus.CREATED).body(newDepartment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error creating department: " + e.getMessage());
        }
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
    public ResponseEntity<?> getDepartmentById(@PathVariable Long id) {
        try {
            Department department = departmentService.getDepartmentById(id);
            if (department != null) {
                return ResponseEntity.ok(department);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Department not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching department");
        }
    }

    @GetMapping("/get-all")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
    public ResponseEntity<?> getAllDepartments() {
        try {
            List<Department> departments = departmentService.getAllDepartments();
            return ResponseEntity.ok(departments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching departments");
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updateDepartment(@PathVariable Long id, @RequestBody Department department) {
        try {
            Department updatedDepartment = departmentService.updateDepartment(id, department);
            if (updatedDepartment != null) {
                return ResponseEntity.ok(updatedDepartment);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Department not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating department");
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteDepartment(@PathVariable Long id) {
        try {
            departmentService.deleteDepartment(id);
            return ResponseEntity.ok("Department deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting department");
        }
    }
}
