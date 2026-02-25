package Israt.example.Hr.and.payroll.management.system.backend.service;

import Israt.example.Hr.and.payroll.management.system.backend.entity.Payslip;
import Israt.example.Hr.and.payroll.management.system.backend.entity.Employee;
import Israt.example.Hr.and.payroll.management.system.backend.repository.PayslipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PayslipService {

    @Autowired
    private PayslipRepository payslipRepository;

    @Autowired
    private EmployeeService employeeService;

    public Payslip createPayslip(Payslip payslip) {

        Employee employee = employeeService.getEmployeeById(payslip.getEmployeeId());
        if (employee != null && employee.getBaseSalary() != null) {
            payslip.setBaseSalary(employee.getBaseSalary());
        }
        return payslipRepository.save(payslip);
    }

    public Payslip getPayslipById(Long id) {
        Optional<Payslip> payslip = payslipRepository.findById(id);
        return payslip.orElse(null);
    }

    public List<Payslip> getAllPayslips() {
        return payslipRepository.findAll();
    }

    public List<Payslip> getPayslipsByEmployeeId(Long employeeId) {
        return payslipRepository.findByEmployeeId(employeeId);
    }



    public Payslip updatePayslip(Long id, Payslip payslipDetails) {
        Optional<Payslip> payslip = payslipRepository.findById(id);
        if (payslip.isPresent()) {
            Payslip ps = payslip.get();
            ps.setEmployeeId(payslipDetails.getEmployeeId());
            ps.setManagerId(payslipDetails.getManagerId());
            ps.setMonth(payslipDetails.getMonth());
            ps.setYear(payslipDetails.getYear());
            Employee emp = employeeService.getEmployeeById(ps.getEmployeeId());
            if (emp != null && emp.getBaseSalary() != null) {
                ps.setBaseSalary(emp.getBaseSalary());
            }
            return payslipRepository.save(ps);
        }
        return null;
    }

    public void deletePayslip(Long id) {
        payslipRepository.deleteById(id);
    }
}
