package Israt.example.Hr.and.payroll.management.system.backend.repository;

import Israt.example.Hr.and.payroll.management.system.backend.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByEmployeeId(Long employeeId);
    List<LeaveRequest> findByStatus(String status);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.employeeId = :employeeId AND lr.status = :status")
    List<LeaveRequest> findByEmployeeIdAndStatus(@Param("employeeId") Long employeeId, @Param("status") String status);
}
