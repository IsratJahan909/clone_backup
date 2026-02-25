package Israt.example.Hr.and.payroll.management.system.backend.service;

import Israt.example.Hr.and.payroll.management.system.backend.entity.Payroll;
import Israt.example.Hr.and.payroll.management.system.backend.repository.PayrollRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PayrollService {

    @Autowired
    private PayrollRepository payrollRepository;

    public Payroll createPayroll(Payroll payroll) {
        return payrollRepository.save(payroll);
    }

    public Payroll getPayrollById(Long id) {
        Optional<Payroll> payroll = payrollRepository.findById(id);
        return payroll.orElse(null);
    }

    public List<Payroll> getAllPayrolls() {
        return payrollRepository.findAll();
    }

    public List<Payroll> getPayrollsByEmployeeId(Long employeeId) {
        return payrollRepository.findByEmployeeId(employeeId);
    }

    public List<Payroll> getPayrollsByMonthAndYear(int month, int year) {
        return payrollRepository.findByPayrollMonthAndPayrollYear(month, year);
    }

    public Payroll updatePayroll(Long id, Payroll payrollDetails) {
        Optional<Payroll> payroll = payrollRepository.findById(id);
        if (payroll.isPresent()) {
            Payroll p = payroll.get();
            p.setEmployeeId(payrollDetails.getEmployeeId());
            p.setPayrollMonth(payrollDetails.getPayrollMonth());
            p.setPayrollYear(payrollDetails.getPayrollYear());
            p.setBaseSalary(payrollDetails.getBaseSalary());
            p.setMedicalAllowance(payrollDetails.getMedicalAllowance());
            p.setOvertime(payrollDetails.getOvertime());
            p.setBonus(payrollDetails.getBonus());
            p.setTotalEarnings(payrollDetails.getTotalEarnings());
            p.setIncomeTax(payrollDetails.getIncomeTax());
            p.setProvidentFund(payrollDetails.getProvidentFund());
            p.setOtherDeductions(payrollDetails.getOtherDeductions());
            p.setTotalDeductions(payrollDetails.getTotalDeductions());
            p.setNetSalary(payrollDetails.getNetSalary());
            return payrollRepository.save(p);
        }
        return null;
    }

    public void deletePayroll(Long id) {
        payrollRepository.deleteById(id);
    }

    public Payroll approvePayroll(Long id, Long approvedBy) {
        Optional<Payroll> payroll = payrollRepository.findById(id);
        if (payroll.isPresent()) {
            Payroll p = payroll.get();
            return payrollRepository.save(p);
        }
        return null;
    }
}
