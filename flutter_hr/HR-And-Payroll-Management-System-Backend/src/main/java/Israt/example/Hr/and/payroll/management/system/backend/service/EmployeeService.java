package Israt.example.Hr.and.payroll.management.system.backend.service;

import Israt.example.Hr.and.payroll.management.system.backend.entity.Employee;
import Israt.example.Hr.and.payroll.management.system.backend.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Employee getEmployeeById(Long employeeId) {
        Optional<Employee> employee = employeeRepository.findById(employeeId);
        return employee.orElse(null);
    }

    public Employee getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email).orElse(null);
    }

//    public Employee getEmployeeByCode(String employeeCode) {
//        return employeeRepository.findByEmployeeCode(employeeCode);
//    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee updateEmployee(Long employeeId, Employee employeeDetails) {
        Optional<Employee> employee = employeeRepository.findById(employeeId);
        if (employee.isPresent()) {
            Employee emp = employee.get();
            emp.setFirstName(employeeDetails.getFirstName());
            emp.setLastName(employeeDetails.getLastName());
            emp.setPhoneNumber(employeeDetails.getPhoneNumber());
            emp.setDesignation(employeeDetails.getDesignation());
            emp.setDepartmentId(employeeDetails.getDepartmentId());
            emp.setEmploymentType(employeeDetails.getEmploymentType());
            emp.setBaseSalary(employeeDetails.getBaseSalary());
//            emp.setHouseRentAllowance(employeeDetails.getHouseRentAllowance());
            emp.setMedicalAllowance(employeeDetails.getMedicalAllowance());
//            emp.setDearnesAllowance(employeeDetails.getDearnesAllowance());
            emp.setBankAccountNumber(employeeDetails.getBankAccountNumber());
            emp.setBankName(employeeDetails.getBankName());
            emp.setIsActive(employeeDetails.getIsActive());
            return employeeRepository.save(emp);
        }
        return null;
    }

    public void deleteEmployee(Long employeeId) {
        employeeRepository.deleteById(employeeId);
    }

    public List<Employee> getEmployeesByDepartment(Long departmentId) {
        return employeeRepository.findAll().stream()
            .filter(emp -> emp.getDepartmentId().equals(departmentId))
            .toList();
    }
}
