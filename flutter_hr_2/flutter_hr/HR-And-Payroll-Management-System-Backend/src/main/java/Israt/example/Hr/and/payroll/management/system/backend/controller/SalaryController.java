package Israt.example.Hr.and.payroll.management.system.backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Israt.example.Hr.and.payroll.management.system.backend.entity.Salary;
import Israt.example.Hr.and.payroll.management.system.backend.service.SalaryService;

@RestController
@RequestMapping("/api/salary")
@CrossOrigin(origins = "*")
public class SalaryController {

    @Autowired
    private SalaryService salaryService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Salary> getAllSalaries() {
        return salaryService.getAllSalaries();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
    public ResponseEntity<Salary> getSalaryById(@PathVariable Long id) {
        Optional<Salary> salary = salaryService.getSalaryById(id);
        if (salary.isPresent()) {

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUserEmail = auth.getName();
            if (!salaryService.isUserAdmin(currentUserEmail) &&
                !salaryService.isOwnerOfSalary(id, currentUserEmail)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.ok(salary.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
    public ResponseEntity<?> getSalariesByEmployeeId(@PathVariable Long employeeId) {
        try {

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUserEmail = auth.getName();
            if (!salaryService.isUserAdmin(currentUserEmail) &&
                !salaryService.isUserOwnerOfEmployeeSalaries(employeeId, currentUserEmail)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
            }
            List<Salary> salaries = salaryService.getSalariesByEmployeeId(employeeId);
            return ResponseEntity.ok(salaries);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching salaries");
        }
    }

    @GetMapping("/month/{month}/year/{year}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Salary> getSalariesByMonthAndYear(@PathVariable Integer month, @PathVariable Integer year) {
        return salaryService.getSalariesByMonthAndYear(month, year);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public Salary createSalary(@RequestBody Salary salary) {
        return salaryService.saveSalary(salary);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Salary> updateSalary(@PathVariable Long id, @RequestBody Salary salaryDetails) {
        Optional<Salary> optionalSalary = salaryService.getSalaryById(id);
        if (optionalSalary.isPresent()) {
            Salary salary = optionalSalary.get();

            salary.setEmployeeId(salaryDetails.getEmployeeId());
            salary.setMonth(salaryDetails.getMonth());
            salary.setYear(salaryDetails.getYear());
            salary.setBaseSalary(salaryDetails.getBaseSalary());
            salary.setAdvanceSalary(salaryDetails.getAdvanceSalary());
            salary.setBonusAmount(salaryDetails.getBonusAmount());
            salary.setAllowances(salaryDetails.getAllowances());
            salary.setDeductions(salaryDetails.getDeductions());
            salary.setInsurance(salaryDetails.getInsurance());
            salary.setMedicare(salaryDetails.getMedicare());
            salary.setTax(salaryDetails.getTax());
            salary.setProvidentFund(salaryDetails.getProvidentFund());
            salary.setFoodDeduction(salaryDetails.getFoodDeduction());
            salary.setOtherDeduction(salaryDetails.getOtherDeduction());
            salary.setOvertimeHours(salaryDetails.getOvertimeHours());
            salary.setOvertimeRate(salaryDetails.getOvertimeRate());
            salary.setOvertimePay(salaryDetails.getOvertimePay());
            salary.setGrossSalary(salaryDetails.getGrossSalary());
            salary.setNetSalary(salaryDetails.getNetSalary());
            salary.setStatus(salaryDetails.getStatus());
            salary.setPaymentMethod(salaryDetails.getPaymentMethod());
            salary.setPaymentReference(salaryDetails.getPaymentReference());
            salary.setPaymentDate(salaryDetails.getPaymentDate());
            salary.setNotes(salaryDetails.getNotes());
            salary.setCreatedAt(salaryDetails.getCreatedAt());
            salary.setUpdatedAt(salaryDetails.getUpdatedAt());
            salary.setApprovedBy(salaryDetails.getApprovedBy());
            salary.setApprovedDate(salaryDetails.getApprovedDate());
            Salary updatedSalary = salaryService.saveSalary(salary);
            return ResponseEntity.ok(updatedSalary);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteSalary(@PathVariable Long id) {
        if (salaryService.getSalaryById(id).isPresent()) {
            salaryService.deleteSalary(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
