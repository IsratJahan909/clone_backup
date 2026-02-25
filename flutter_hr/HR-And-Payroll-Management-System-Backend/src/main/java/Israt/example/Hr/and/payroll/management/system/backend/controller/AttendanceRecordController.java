package Israt.example.Hr.and.payroll.management.system.backend.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import Israt.example.Hr.and.payroll.management.system.backend.dto.AttendanceRequest;
import Israt.example.Hr.and.payroll.management.system.backend.entity.AttendanceRecord;
import Israt.example.Hr.and.payroll.management.system.backend.service.AttendanceRecordService;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "*")
public class AttendanceRecordController {

  @Autowired
  private AttendanceRecordService attendanceRecordService;

  // Create (Check In)
  @PostMapping
  @PreAuthorize("permitAll()")
  public ResponseEntity<?> create(@RequestBody AttendanceRequest request) {
    String email = request.getEmail();


    if (email == null && request.getRemarks() != null) {

        if (request.getRemarks().contains("@")) {
            email = request.getRemarks();
            request.setRemarks(null);
        }
    }


    if (email == null) {
         if (SecurityContextHolder.getContext().getAuthentication() != null
             && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
             && !"anonymousUser".equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())) {
             email = SecurityContextHolder.getContext().getAuthentication().getName();
         }
    }

    if (email == null) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email is required.");
    }

    AttendanceRecord attendanceRecord = mapToEntity(request);


    if (!attendanceRecordService.isUserAdmin(email)) {
        Long empId = attendanceRecordService.getEmployeeIdByEmail(email);
        if (empId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Employee record not found for the current user.");
        }
        attendanceRecord.setEmployeeId(empId);
    } else {
        if (attendanceRecord.getEmployeeId() == null) {
             Long empId = attendanceRecordService.getEmployeeIdByEmail(email);
             if (empId != null) {
                 attendanceRecord.setEmployeeId(empId);
             } else {
                 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Employee ID is required.");
             }
        }
    }

    try {
        AttendanceRecord saved = attendanceRecordService.createAttendanceRecord(attendanceRecord);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating attendance record: " + e.getMessage());
    }
  }

  // Get by ID
  @GetMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> getById(@PathVariable Long id) {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    AttendanceRecord record = attendanceRecordService.getAttendanceRecordById(id);

    if (record == null) {
      return ResponseEntity.notFound().build();
    }

    if (!attendanceRecordService.isUserAdmin(email) &&
      !attendanceRecordService.isOwnerOfAttendanceRecord(id, email)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
    }

    return ResponseEntity.ok(record);
  }


    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getList(
      @RequestParam(required = false) Long employeeId,
      @RequestParam(required = false) String date_gte,
      @RequestParam(required = false) String date_lte,
      @RequestParam(defaultValue = "1") int _page,
      @RequestParam(defaultValue = "10") int _limit) {
  
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      String email = auth.getName();
      boolean isAdmin = auth.getAuthorities().stream()
              .anyMatch(a -> a.getAuthority().equals("ADMIN"));
  
      // Ownership logic
      if (employeeId != null) {
        if (!isAdmin && !attendanceRecordService.isUserAdminOrOwner(email, employeeId)) {
          return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
      } else if (!isAdmin) {
        Long empId = attendanceRecordService.getEmployeeIdByEmail(email);
        if (empId == null) {
          return ResponseEntity.notFound().build();
        }
        employeeId = empId;
      }

    // Parse dates (supports both YYYY-MM-DD and ISO format)
    LocalDate start = null;
    LocalDate end = null;
    if (date_gte != null) {
      start = LocalDate.parse(date_gte.split("T")[0]);
    }
    if (date_lte != null) {
      end = LocalDate.parse(date_lte.split("T")[0]);
    }

    Pageable pageable = PageRequest.of(_page - 1, _limit);
    Page<AttendanceRecord> page = attendanceRecordService.getFiltered(employeeId, start, end, pageable);

    return ResponseEntity.ok()
      .header("X-Total-Count", String.valueOf(page.getTotalElements()))
      .body(page.getContent());
  }


  @PutMapping("/{id}")
  @PreAuthorize("permitAll()")
  public ResponseEntity<?> update(@PathVariable Long id, @RequestBody AttendanceRequest request) {
    String email = request.getEmail();


    if (email == null && request.getRemarks() != null) {
        if (request.getRemarks().contains("@")) {
            email = request.getRemarks();
            request.setRemarks(null);
        }
    }

    if (email == null) {
         // Try to get from security context if available (e.g. Admin editing)
         if (SecurityContextHolder.getContext().getAuthentication() != null
             && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
             && !"anonymousUser".equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())) {
             email = SecurityContextHolder.getContext().getAuthentication().getName();
         }
    }

    if (email == null) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email is required for verification.");
    }

    if (!attendanceRecordService.isOwnerOfAttendanceRecord(id, email)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied. Only the employee can update their attendance.");
    }

    AttendanceRecord details = mapToEntity(request);
    AttendanceRecord updated = attendanceRecordService.updateAttendanceRecord(id, details);
    if (updated == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(updated);
  }

  // Delete
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<?> delete(@PathVariable Long id) {
    attendanceRecordService.deleteAttendanceRecord(id);
    return ResponseEntity.ok().build();
  }

  private AttendanceRecord mapToEntity(AttendanceRequest request) {
      AttendanceRecord record = new AttendanceRecord();
      record.setEmployeeId(request.getEmployeeId());
      record.setDate(request.getDate());
      record.setClockInTime(request.getClockInTime());
      record.setClockOutTime(request.getClockOutTime());
      record.setStatus(request.getStatus());
      record.setRemarks(request.getRemarks());
      record.setIsOvertime(request.getIsOvertime());
      record.setOvertimeHours(request.getOvertimeHours());
      record.setOvertimeReason(request.getOvertimeReason());
      return record;
  }
}
