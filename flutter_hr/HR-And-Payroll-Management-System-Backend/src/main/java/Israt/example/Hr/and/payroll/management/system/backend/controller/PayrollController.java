package Israt.example.Hr.and.payroll.management.system.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Israt.example.Hr.and.payroll.management.system.backend.entity.Payroll;
import Israt.example.Hr.and.payroll.management.system.backend.service.PayrollService;

@RestController
@RequestMapping("/api/salaries")
@CrossOrigin(origins = "*")
public class PayrollController {

    @Autowired
    private PayrollService payrollService;

    @PostMapping
    public ResponseEntity<?> createPayroll(@RequestBody Payroll payroll) {
        try {
            Payroll newPayroll = payrollService.createPayroll(payroll);
            return ResponseEntity.status(HttpStatus.CREATED).body(newPayroll);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error creating payroll: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPayrollById(@PathVariable Long id) {
        try {
            Payroll payroll = payrollService.getPayrollById(id);
            if (payroll != null) {
                return ResponseEntity.ok(payroll);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payroll not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching payroll");
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllPayrolls() {
        try {
            List<Payroll> payrolls = payrollService.getAllPayrolls();
            return ResponseEntity.ok(payrolls);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching payrolls");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePayroll(@PathVariable Long id, @RequestBody Payroll payroll) {
        try {
            Payroll updatedPayroll = payrollService.updatePayroll(id, payroll);
            if (updatedPayroll != null) {
                return ResponseEntity.ok(updatedPayroll);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payroll not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating payroll");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePayroll(@PathVariable Long id) {
        try {
            payrollService.deletePayroll(id);
            return ResponseEntity.ok("Payroll deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting payroll");
        }
    }

    @PostMapping("/process")
    public ResponseEntity<?> processSalary(@RequestBody Map<String, Object> request) {
        try {
            Long employeeId = Long.valueOf((String) request.get("employeeId"));
            int month = Integer.valueOf((String) request.get("month"));
            int year = Integer.valueOf((String) request.get("year"));
            Payroll payroll = new Payroll();
            payroll.setEmployeeId(employeeId);
            payroll.setPayrollMonth(month);
            payroll.setPayrollYear(year);
            Payroll processedPayroll = payrollService.createPayroll(payroll);
            return ResponseEntity.ok(processedPayroll);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid number format in input data: " + e.getMessage());
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing required input data");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing salary: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approvePayroll(@PathVariable Long id, @RequestBody Map<String, Long> request) {
        try {
            Long approvedBy = request.get("approvedBy");
            Payroll approvedPayroll = payrollService.approvePayroll(id, approvedBy);
            if (approvedPayroll != null) {
                return ResponseEntity.ok(approvedPayroll);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payroll not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error approving payroll");
        }
    }
}
