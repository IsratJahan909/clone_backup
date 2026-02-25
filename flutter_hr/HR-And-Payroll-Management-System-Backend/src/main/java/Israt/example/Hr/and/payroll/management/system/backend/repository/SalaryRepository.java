package Israt.example.Hr.and.payroll.management.system.backend.repository;

import Israt.example.Hr.and.payroll.management.system.backend.entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {
    List<Salary> findByEmployeeId(Long employeeId);
    List<Salary> findByMonthAndYear(Integer month, Integer year);
}