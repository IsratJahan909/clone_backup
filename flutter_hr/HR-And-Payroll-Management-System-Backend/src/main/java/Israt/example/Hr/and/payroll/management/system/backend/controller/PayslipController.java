package Israt.example.Hr.and.payroll.management.system.backend.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Israt.example.Hr.and.payroll.management.system.backend.entity.Payslip;
import Israt.example.Hr.and.payroll.management.system.backend.service.PayslipService;

@RestController
@RequestMapping("/api/payslip")
@CrossOrigin(origins = "*")
public class PayslipController {

    @Autowired
    private PayslipService payslipService;

    @PostMapping
    public ResponseEntity<?> createPayslip(@RequestBody Payslip payslip) {
        try {
            Payslip newPayslip = payslipService.createPayslip(payslip);
            return ResponseEntity.status(HttpStatus.CREATED).body(newPayslip);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error creating payslip: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPayslipById(@PathVariable Long id) {
        try {
            Payslip payslip = payslipService.getPayslipById(id);
            if (payslip != null) {
                return ResponseEntity.ok(payslip);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payslip not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching payslip");
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllPayslips(@RequestParam(required = false) Long employeeId) {
        try {
            List<Payslip> payslips;
            if (employeeId != null) {
                payslips = payslipService.getPayslipsByEmployeeId(employeeId);
            } else {
                payslips = payslipService.getAllPayslips();
            }
            return ResponseEntity.ok(payslips);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching payslips");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePayslip(@PathVariable Long id, @RequestBody Payslip payslip) {
        try {
            Payslip updatedPayslip = payslipService.updatePayslip(id, payslip);
            if (updatedPayslip != null) {
                return ResponseEntity.ok(updatedPayslip);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payslip not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating payslip");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePayslip(@PathVariable Long id) {
        try {
            payslipService.deletePayslip(id);
            return ResponseEntity.ok("Payslip deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting payslip");
        }
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<?> downloadPayslip(@PathVariable Long id) {
        try {
            Payslip payslip = payslipService.getPayslipById(id);
            if (payslip != null) {
                return ResponseEntity.ok(payslip);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payslip not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error downloading payslip");
        }
    }
}
