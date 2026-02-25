package Israt.example.Hr.and.payroll.management.system.backend.repository;

import Israt.example.Hr.and.payroll.management.system.backend.entity.AdvanceSalary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AdvanceSalaryRepository extends JpaRepository<AdvanceSalary, Long> {
    List<AdvanceSalary> findByEmployeeId(Long employeeId);
    List<AdvanceSalary> findByStatus(String status);
}