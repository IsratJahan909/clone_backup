package Israt.example.Hr.and.payroll.management.system.backend.service;

import Israt.example.Hr.and.payroll.management.system.backend.entity.Employee;
import Israt.example.Hr.and.payroll.management.system.backend.entity.Salary;
import Israt.example.Hr.and.payroll.management.system.backend.entity.User;
import Israt.example.Hr.and.payroll.management.system.backend.enums.UserRole;
import Israt.example.Hr.and.payroll.management.system.backend.repository.EmployeeRepository;
import Israt.example.Hr.and.payroll.management.system.backend.repository.SalaryRepository;
import Israt.example.Hr.and.payroll.management.system.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SalaryService {

    @Autowired
    private SalaryRepository salaryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Salary> getAllSalaries() {
        return salaryRepository.findAll();
    }

    public Optional<Salary> getSalaryById(Long id) {
        return salaryRepository.findById(id);
    }

    public List<Salary> getSalariesByEmployeeId(Long employeeId) {
        return salaryRepository.findByEmployeeId(employeeId);
    }

    public List<Salary> getSalariesByMonthAndYear(Integer month, Integer year) {
        return salaryRepository.findByMonthAndYear(month, year);
    }

    public Salary saveSalary(Salary salary) {
        return salaryRepository.save(salary);
    }

    public void deleteSalary(Long id) {
        salaryRepository.deleteById(id);
    }

    public boolean isOwnerOfSalary(Long salaryId, String userEmail) {
        Optional<Salary> salary = salaryRepository.findById(salaryId);
        if (salary.isPresent()) {
            Long employeeId = salary.get().getEmployeeId();
            Optional<Employee> employee = employeeRepository.findById(employeeId);
            if (employee.isPresent()) {
                return employee.get().getEmail().equals(userEmail);
            }
        }
        return false;
    }

    public boolean isUserOwnerOfEmployeeSalaries(Long employeeId, String userEmail) {
        Optional<Employee> employee = employeeRepository.findById(employeeId);
        if (employee.isPresent()) {
            return employee.get().getEmail().equals(userEmail);
        }
        return false;
    }

    public boolean isUserAdmin(String userEmail) {
        Optional<User> user = userRepository.findByEmail(userEmail);
        return user.isPresent() && user.get().getRole() == UserRole.ADMIN;
    }
}