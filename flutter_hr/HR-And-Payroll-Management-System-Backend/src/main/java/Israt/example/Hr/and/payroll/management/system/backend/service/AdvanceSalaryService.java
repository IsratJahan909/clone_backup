package Israt.example.Hr.and.payroll.management.system.backend.service;

import Israt.example.Hr.and.payroll.management.system.backend.entity.AdvanceSalary;
import Israt.example.Hr.and.payroll.management.system.backend.entity.Employee;
import Israt.example.Hr.and.payroll.management.system.backend.entity.User;
import Israt.example.Hr.and.payroll.management.system.backend.enums.UserRole;
import Israt.example.Hr.and.payroll.management.system.backend.repository.AdvanceSalaryRepository;
import Israt.example.Hr.and.payroll.management.system.backend.repository.EmployeeRepository;
import Israt.example.Hr.and.payroll.management.system.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AdvanceSalaryService {

    @Autowired
    private AdvanceSalaryRepository advanceSalaryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public AdvanceSalary createAdvanceSalary(AdvanceSalary advanceSalary) {
        return advanceSalaryRepository.save(advanceSalary);
    }

    public AdvanceSalary getAdvanceSalaryById(Long id) {
        Optional<AdvanceSalary> advanceSalary = advanceSalaryRepository.findById(id);
        return advanceSalary.orElse(null);
    }

    public List<AdvanceSalary> getAllAdvanceSalaries() {
        return advanceSalaryRepository.findAll();
    }

    public List<AdvanceSalary> getAdvanceSalariesByEmployeeId(Long employeeId) {
        return advanceSalaryRepository.findByEmployeeId(employeeId);
    }

    public List<AdvanceSalary> getAdvanceSalariesByStatus(String status) {
        return advanceSalaryRepository.findByStatus(status);
    }

    public AdvanceSalary updateAdvanceSalary(Long id, AdvanceSalary advanceSalaryDetails) {
        Optional<AdvanceSalary> advanceSalary = advanceSalaryRepository.findById(id);
        if (advanceSalary.isPresent()) {
            AdvanceSalary as = advanceSalary.get();
            as.setEmployeeId(advanceSalaryDetails.getEmployeeId());
            as.setAmount(advanceSalaryDetails.getAmount());
            as.setRequestReason(advanceSalaryDetails.getRequestReason());
            as.setStatus(advanceSalaryDetails.getStatus());
            as.setApprovedBy(advanceSalaryDetails.getApprovedBy());
            return advanceSalaryRepository.save(as);
        }
        return null;
    }

    public void deleteAdvanceSalary(Long id) {
        advanceSalaryRepository.deleteById(id);
    }

    public AdvanceSalary approveAdvanceSalary(Long id, Long approvedBy) {
        Optional<AdvanceSalary> advanceSalary = advanceSalaryRepository.findById(id);
        if (advanceSalary.isPresent()) {
            AdvanceSalary as = advanceSalary.get();
            as.setStatus("Approved");
            as.setApprovedBy(approvedBy);
            as.setApprovalDate(java.time.LocalDateTime.now());
            return advanceSalaryRepository.save(as);
        }
        return null;
    }

    public AdvanceSalary rejectAdvanceSalary(Long id, Long rejectedBy) {
        Optional<AdvanceSalary> advanceSalary = advanceSalaryRepository.findById(id);
        if (advanceSalary.isPresent()) {
            AdvanceSalary as = advanceSalary.get();
            as.setStatus("Rejected");
            as.setRejectedBy(rejectedBy);
            as.setApprovalDate(java.time.LocalDateTime.now()); // Reuse approvalDate for when it was handled
            return advanceSalaryRepository.save(as);
        }
        return null;
    }

    public boolean isOwnerOfAdvanceSalary(Long advanceSalaryId, String userEmail) {
        Optional<AdvanceSalary> advanceSalary = advanceSalaryRepository.findById(advanceSalaryId);
        if (advanceSalary.isPresent()) {
            Long employeeId = advanceSalary.get().getEmployeeId();
            Optional<Employee> employee = employeeRepository.findById(employeeId);
            if (employee.isPresent()) {
                return employee.get().getEmail().equals(userEmail);
            }
        }
        return false;
    }

    public boolean isUserAdminOrOwner(String userEmail, Long employeeId) {
        Optional<User> user = userRepository.findByEmail(userEmail);
        if (user.isPresent()) {
            if (user.get().getRole() == UserRole.ADMIN) {
                return true;
            }
            Optional<Employee> employee = employeeRepository.findById(employeeId);
            if (employee.isPresent()) {
                return employee.get().getEmail().equals(userEmail);
            }
        }
        return false;
    }

    public boolean isUserAdmin(String userEmail) {
        Optional<User> user = userRepository.findByEmail(userEmail);
        if (user.isPresent() && user.get().getRole() == UserRole.ADMIN) return true;
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            return auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMIN"));
        }
        return false;
    }

    public Long getEmployeeIdByEmail(String userEmail) {
        Optional<Employee> employee = employeeRepository.findByEmail(userEmail);
        return employee.map(Employee::getEmployeeId).orElse(null);
    }
}