package Israt.example.Hr.and.payroll.management.system.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Israt.example.Hr.and.payroll.management.system.backend.entity.LeaveRequest;
import Israt.example.Hr.and.payroll.management.system.backend.service.LeaveRequestService;


@RestController
@RequestMapping("/api/leave-requests")
@CrossOrigin(origins = "*")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<?> createLeaveRequest(@RequestBody LeaveRequest leaveRequest) {
        try {
            LeaveRequest newLeaveRequest = leaveRequestService.createLeaveRequest(leaveRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(newLeaveRequest);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error creating leave request: " + e.getMessage());
        }
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
    public ResponseEntity<?> getLeaveRequestById(@PathVariable Long id) {
        try {
            LeaveRequest leaveRequest = leaveRequestService.getLeaveRequestById(id);
            if (leaveRequest != null) {
                // Check ownership for employees
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                String currentUserEmail = auth.getName();
                if (!leaveRequestService.isUserAdmin(currentUserEmail) &&
                    !leaveRequestService.isOwnerOfLeaveRequest(id, currentUserEmail)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
                }
                return ResponseEntity.ok(leaveRequest);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Leave request not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching leave request");
        }
    }

    @GetMapping("/get-all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getAllLeaveRequests() {
        try {
            List<LeaveRequest> leaveRequests = leaveRequestService.getAllLeaveRequests();
            return ResponseEntity.ok(leaveRequests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching leave requests");
        }
    }

    @GetMapping("/get-by-employee/{employeeId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
    public ResponseEntity<?> getLeaveRequestsByEmployeeId(@PathVariable Long employeeId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUserEmail = auth.getName();
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMIN"));
                    
            if (!isAdmin && !leaveRequestService.isUserOwnerOfEmployeeLeaveRequests(employeeId, currentUserEmail)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
            }
            List<LeaveRequest> leaveRequests = leaveRequestService.getLeaveRequestsByEmployeeId(employeeId);
            return ResponseEntity.ok(leaveRequests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching leave requests");
        }
    }

    @GetMapping("/get-by-status/{status}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getLeaveRequestsByStatus(@PathVariable String status) {
        try {
            List<LeaveRequest> leaveRequests = leaveRequestService.getLeaveRequestsByStatus(status);
            return ResponseEntity.ok(leaveRequests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching leave requests");
        }
    }

    @GetMapping("/get-by-employee-status/{employeeId}/{status}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
    public ResponseEntity<?> getLeaveRequestsByEmployeeIdAndStatus(
            @PathVariable Long employeeId,
            @PathVariable String status) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUserEmail = auth.getName();
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMIN"));
                    
            if (!isAdmin && !leaveRequestService.isUserOwnerOfEmployeeLeaveRequests(employeeId, currentUserEmail)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
            }
            List<LeaveRequest> leaveRequests = leaveRequestService.getLeaveRequestsByEmployeeIdAndStatus(employeeId, status);
            return ResponseEntity.ok(leaveRequests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching leave requests");
        }
    }

    @GetMapping("/get-pending")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getPendingLeaveRequests() {
        try {
            List<LeaveRequest> leaveRequests = leaveRequestService.getPendingLeaveRequests();
            return ResponseEntity.ok(leaveRequests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching pending leave requests");
        }
    }

    @GetMapping("/get-approved")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getApprovedLeaveRequests() {
        try {
            List<LeaveRequest> leaveRequests = leaveRequestService.getApprovedLeaveRequests();
            return ResponseEntity.ok(leaveRequests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching approved leave requests");
        }
    }

    @GetMapping("/get-rejected")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getRejectedLeaveRequests() {
        try {
            List<LeaveRequest> leaveRequests = leaveRequestService.getRejectedLeaveRequests();
            return ResponseEntity.ok(leaveRequests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching rejected leave requests");
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('EMPLOYEE') and @leaveRequestService.isOwnerOfLeaveRequest(#id, authentication.name))")
    public ResponseEntity<?> updateLeaveRequest(@PathVariable Long id, @RequestBody LeaveRequest leaveRequest) {
        try {
            LeaveRequest updatedLeaveRequest = leaveRequestService.updateLeaveRequest(id, leaveRequest);
            if (updatedLeaveRequest != null) {
                return ResponseEntity.ok(updatedLeaveRequest);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Leave request not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating leave request");
        }
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> approveLeaveRequest(@PathVariable Long id, @RequestBody LeaveRequest approvalDetails) {
        try {
            LeaveRequest approvedLeaveRequest = leaveRequestService.approveLeaveRequest(id, approvalDetails);
            if (approvedLeaveRequest != null) {
                return ResponseEntity.ok(approvedLeaveRequest);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Leave request not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error approving leave request");
        }
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> rejectLeaveRequest(@PathVariable Long id, @RequestBody LeaveRequest rejectionDetails) {
        try {
            LeaveRequest rejectedLeaveRequest = leaveRequestService.rejectLeaveRequest(id, rejectionDetails);
            if (rejectedLeaveRequest != null) {
                return ResponseEntity.ok(rejectedLeaveRequest);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Leave request not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error rejecting leave request");
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteLeaveRequest(@PathVariable Long id) {
        try {
            leaveRequestService.deleteLeaveRequest(id);
            return ResponseEntity.ok("Leave request deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting leave request");
        }
    }
}
