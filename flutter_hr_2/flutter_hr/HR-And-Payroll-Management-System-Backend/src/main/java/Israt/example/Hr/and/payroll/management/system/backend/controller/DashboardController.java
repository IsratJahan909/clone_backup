package Israt.example.Hr.and.payroll.management.system.backend.controller;

import Israt.example.Hr.and.payroll.management.system.backend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Autowired
    private AttendanceRecordService attendanceRecordService;

    @Autowired
    private AdvanceSalaryService advanceSalaryService;

    @Autowired
    private PayrollService payrollService;

    @Autowired
    private ProfileEditRequestService profileEditRequestService;

    @GetMapping("/admin-dashboard")
    public ResponseEntity<?> getAdminDashboard() {
        try {
            Map<String, Object> dashboard = new HashMap<>();

            // Total users
            dashboard.put("totalUsers", userService.getAllUsers().size());

            // Total employees
            dashboard.put("totalEmployees", employeeService.getAllEmployees().size());

            // Total departments
            dashboard.put("totalDepartments", departmentService.getAllDepartments().size());

            // Pending leave requests
            dashboard.put("pendingLeaveRequests", leaveRequestService.getPendingLeaveRequests().size());

            // Approved leave requests
            dashboard.put("approvedLeaveRequests", leaveRequestService.getApprovedLeaveRequests().size());

            // Rejected leave requests
            dashboard.put("rejectedLeaveRequests", leaveRequestService.getRejectedLeaveRequests().size());

            // Pending profile edit requests
            dashboard.put("pendingProfileEdits", profileEditRequestService.getProfileEditRequestsByStatus("Pending").size());

            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching admin dashboard");
        }
    }

    @GetMapping("/employee-dashboard/{employeeId}")
    public ResponseEntity<?> getEmployeeDashboard(@PathVariable Long employeeId) {
        try {
            Map<String, Object> dashboard = new HashMap<>();

            // Employee info
            dashboard.put("employee", employeeService.getEmployeeById(employeeId));

            // Recent attendance
            dashboard.put("recentAttendance", attendanceRecordService.getAttendanceRecordsByEmployeeId(employeeId));

            // Leave requests
            dashboard.put("leaveRequests", leaveRequestService.getLeaveRequestsByEmployeeId(employeeId));

            // Advance salaries
            dashboard.put("advanceSalaries", advanceSalaryService.getAdvanceSalariesByEmployeeId(employeeId));

            // Payrolls
            dashboard.put("payrolls", payrollService.getPayrollsByEmployeeId(employeeId));

            // Profile edit requests
            dashboard.put("profileEditRequests", profileEditRequestService.getProfileEditRequestsByUserId(employeeId));

            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching employee dashboard");
        }
    }
}
