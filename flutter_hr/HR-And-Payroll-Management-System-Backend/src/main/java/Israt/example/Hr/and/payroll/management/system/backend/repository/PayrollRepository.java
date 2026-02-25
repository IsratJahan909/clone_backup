package Israt.example.Hr.and.payroll.management.system.backend.repository;

import Israt.example.Hr.and.payroll.management.system.backend.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Long> {
    List<Payroll> findByEmployeeId(Long employeeId);
    List<Payroll> findByPayrollMonthAndPayrollYear(int month, int year);
}