package Israt.example.Hr.and.payroll.management.system.backend.repository;

import Israt.example.Hr.and.payroll.management.system.backend.entity.AttendanceRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {
    List<AttendanceRecord> findByEmployeeId(Long employeeId);
    List<AttendanceRecord> findByEmployeeIdAndDate(Long employeeId, LocalDate date);

  @Query("SELECT a FROM AttendanceRecord a WHERE (:employeeId IS NULL OR a.employeeId = :employeeId) " +
    "AND (:startDate IS NULL OR a.date >= :startDate) " +
    "AND (:endDate IS NULL OR a.date <= :endDate)")
  Page<AttendanceRecord> findFiltered(@Param("employeeId") Long employeeId,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate,
                                      Pageable pageable);
}
