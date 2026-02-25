package Israt.example.Hr.and.payroll.management.system.backend.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import Israt.example.Hr.and.payroll.management.system.backend.entity.AttendanceRecord;
import Israt.example.Hr.and.payroll.management.system.backend.entity.Employee;
import Israt.example.Hr.and.payroll.management.system.backend.entity.User;
import Israt.example.Hr.and.payroll.management.system.backend.enums.UserRole;
import Israt.example.Hr.and.payroll.management.system.backend.repository.AttendanceRecordRepository;
import Israt.example.Hr.and.payroll.management.system.backend.repository.EmployeeRepository;
import Israt.example.Hr.and.payroll.management.system.backend.repository.UserRepository;

@Service
public class AttendanceRecordService {

    @Autowired
    private AttendanceRecordRepository attendanceRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    // Create new attendance record
    public AttendanceRecord createAttendanceRecord(AttendanceRecord attendanceRecord) {

      if (attendanceRecord.getClockInTime() == null) {
        attendanceRecord.setClockInTime(LocalDateTime.now());
      }

      if (attendanceRecord.getDate() == null) {
          attendanceRecord.setDate(LocalDate.now());
      }

      calculateWorkHours(attendanceRecord);
      setAuditFields(attendanceRecord, true);

      return attendanceRecordRepository.save(attendanceRecord);
    }

    public AttendanceRecord getAttendanceRecordById(Long id) {
        return attendanceRecordRepository.findById(id).orElse(null);
    }

    public AttendanceRecord updateAttendanceRecord(Long id, AttendanceRecord details) {
        return attendanceRecordRepository.findById(id).map(ar -> {
            if (details.getClockInTime() != null) {
                ar.setClockInTime(details.getClockInTime());
            }

            if (details.getClockOutTime() != null) {
                ar.setClockOutTime(details.getClockOutTime());
            }

            if (details.getStatus() != null) ar.setStatus(details.getStatus());
            if (details.getRemarks() != null) ar.setRemarks(details.getRemarks());
            if (details.getIsOvertime() != null) ar.setIsOvertime(details.getIsOvertime());
            if (details.getOvertimeHours() != null) ar.setOvertimeHours(details.getOvertimeHours());
            if (details.getOvertimeReason() != null) ar.setOvertimeReason(details.getOvertimeReason());

            calculateWorkHours(ar);
            setAuditFields(ar, false);

            return attendanceRecordRepository.save(ar);
        }).orElse(null);
    }

    public Page<AttendanceRecord> getFiltered(Long employeeId, LocalDate start, LocalDate end, Pageable pageable) {
        return attendanceRecordRepository.findFiltered(employeeId, start, end, pageable);
    }

    public List<AttendanceRecord> getAttendanceRecordsByEmployeeId(Long employeeId) {
        return attendanceRecordRepository.findByEmployeeId(employeeId);
    }

    public void deleteAttendanceRecord(Long id) {
        attendanceRecordRepository.deleteById(id);
    }

    private void calculateWorkHours(AttendanceRecord ar) {
        if (ar.getClockInTime() != null && ar.getClockOutTime() != null) {
            Duration duration = Duration.between(ar.getClockInTime(), ar.getClockOutTime());
            double hours = duration.toMinutes() / 60.0;
            ar.setWorkHours(Math.round(hours * 100.0) / 100.0);
        } else {
            ar.setWorkHours(null);
        }
    }

    private void setAuditFields(AttendanceRecord ar, boolean isCreate) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) {
            Optional<User> user = userRepository.findByEmail(auth.getName());
            if (user.isPresent()) {
                if (isCreate) {
                    ar.setCreatedBy(user.get());
                }
                ar.setUpdatedBy(user.get());
            }
        }
    }

    public boolean isOwnerOfAttendanceRecord(Long attendanceId, String userEmail) {
        Optional<AttendanceRecord> attendance = attendanceRecordRepository.findById(attendanceId);
        if (attendance.isPresent()) {
            Long employeeId = attendance.get().getEmployeeId();
            Optional<Employee> employee = employeeRepository.findById(employeeId);
            if (employee.isPresent()) {
                String empEmail = employee.get().getEmail();
                return empEmail != null && empEmail.trim().equalsIgnoreCase(userEmail.trim());
            }
        }
        return false;
    }

    public boolean isUserAdminOrOwner(String userEmail, Long employeeId) {
        if (isUserAdmin(userEmail)) return true;
        Optional<Employee> employee = employeeRepository.findById(employeeId);
        if (employee.isPresent()) {
            String empEmail = employee.get().getEmail();
            return empEmail != null && empEmail.trim().equalsIgnoreCase(userEmail.trim());
        }
        return false;
    }

    public boolean isUserAdmin(String userEmail) {
        Optional<User> user = userRepository.findByEmail(userEmail);
        if (user.isPresent()) return user.get().getRole() == UserRole.ADMIN;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            return auth.getAuthorities().stream()
                    .anyMatch(a -> "ADMIN".equalsIgnoreCase(a.getAuthority()) || "ROLE_ADMIN".equalsIgnoreCase(a.getAuthority()));
        }
        return false;
    }

    public Long getEmployeeIdByEmail(String userEmail) {
        Optional<Employee> employee = employeeRepository.findByEmail(userEmail);
        return employee.map(Employee::getEmployeeId).orElse(null);
    }
}
