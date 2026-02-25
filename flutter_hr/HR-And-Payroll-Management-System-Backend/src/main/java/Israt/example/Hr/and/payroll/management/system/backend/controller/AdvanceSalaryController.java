package Israt.example.Hr.and.payroll.management.system.backend.controller;

import Israt.example.Hr.and.payroll.management.system.backend.entity.AdvanceSalary;
import Israt.example.Hr.and.payroll.management.system.backend.service.AdvanceSalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/advanceSalary")
@CrossOrigin(origins = "*")
public class AdvanceSalaryController {

    @Autowired
    private AdvanceSalaryService advanceSalaryService;

    @PostMapping
    @PreAuthorize("hasAuthority('EMPLOYEE') or hasAuthority('ADMIN')")
    public ResponseEntity<?> createAdvanceSalary(@RequestBody AdvanceSalary advanceSalary) {
        try {
            AdvanceSalary newAdvanceSalary = advanceSalaryService.createAdvanceSalary(advanceSalary);
            return ResponseEntity.status(HttpStatus.CREATED).body(newAdvanceSalary);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error creating advance salary: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
    public ResponseEntity<?> getAdvanceSalaryById(@PathVariable Long id) {
        try {
            AdvanceSalary advanceSalary = advanceSalaryService.getAdvanceSalaryById(id);
            if (advanceSalary != null) {
                // Check ownership for employees
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                String currentUserEmail = auth.getName();
                if (!advanceSalaryService.isUserAdmin(currentUserEmail) &&
                    !advanceSalaryService.isOwnerOfAdvanceSalary(id, currentUserEmail)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
                }
                return ResponseEntity.ok(advanceSalary);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Advance salary not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching advance salary");
        }
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
    public ResponseEntity<?> getAllAdvanceSalaries(@RequestParam(required = false) Long employeeId) {
        try {
            List<AdvanceSalary> advanceSalaries;
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUserEmail = auth.getName();
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMIN"));

            if (isAdmin) {
                if (employeeId != null) {
                    advanceSalaries = advanceSalaryService.getAdvanceSalariesByEmployeeId(employeeId);
                } else {
                    advanceSalaries = advanceSalaryService.getAllAdvanceSalaries();
                }
            } else {
                if (employeeId != null) {
                    if (!advanceSalaryService.isUserAdminOrOwner(currentUserEmail, employeeId)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
                    }
                    advanceSalaries = advanceSalaryService.getAdvanceSalariesByEmployeeId(employeeId);
                } else {
                    Long currentEmployeeId = advanceSalaryService.getEmployeeIdByEmail(currentUserEmail);
                    if (currentEmployeeId == null) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found for current user");
                    }
                    advanceSalaries = advanceSalaryService.getAdvanceSalariesByEmployeeId(currentEmployeeId);
                }
            }
            return ResponseEntity.ok(advanceSalaries);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching advance salaries");
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updateAdvanceSalary(@PathVariable Long id, @RequestBody AdvanceSalary advanceSalary) {
        try {
            AdvanceSalary updatedAdvanceSalary = advanceSalaryService.updateAdvanceSalary(id, advanceSalary);
            if (updatedAdvanceSalary != null) {
                return ResponseEntity.ok(updatedAdvanceSalary);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Advance salary not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating advance salary");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteAdvanceSalary(@PathVariable Long id) {
        try {
            advanceSalaryService.deleteAdvanceSalary(id);
            return ResponseEntity.ok("Advance salary deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting advance salary");
        }
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> approveAdvanceSalary(@PathVariable Long id, @RequestBody Map<String, Long> request) {
        try {
            Long approvedBy = request.get("approvedBy");
            AdvanceSalary approvedAdvanceSalary = advanceSalaryService.approveAdvanceSalary(id, approvedBy);
            if (approvedAdvanceSalary != null) {
                return ResponseEntity.ok(approvedAdvanceSalary);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Advance salary not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error approving advance salary");
        }
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> rejectAdvanceSalary(@PathVariable Long id, @RequestBody Map<String, Long> request) {
        try {
            Long rejectedBy = request.get("rejectedBy");
            System.out.println("Rejecting Advance Salary ID: " + id + " by user: " + rejectedBy);
            AdvanceSalary rejectedAdvanceSalary = advanceSalaryService.rejectAdvanceSalary(id, rejectedBy);
            if (rejectedAdvanceSalary != null) {
                return ResponseEntity.ok(rejectedAdvanceSalary);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Advance salary not found");
        } catch (Exception e) {
            System.err.println("Error rejecting advance salary: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error rejecting advance salary");
        }
    }
}
