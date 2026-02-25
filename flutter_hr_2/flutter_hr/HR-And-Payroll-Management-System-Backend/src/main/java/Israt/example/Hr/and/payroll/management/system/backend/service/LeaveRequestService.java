package Israt.example.Hr.and.payroll.management.system.backend.service;

import Israt.example.Hr.and.payroll.management.system.backend.entity.Employee;
import Israt.example.Hr.and.payroll.management.system.backend.entity.LeaveRequest;
import Israt.example.Hr.and.payroll.management.system.backend.entity.User;
import Israt.example.Hr.and.payroll.management.system.backend.enums.UserRole;
import Israt.example.Hr.and.payroll.management.system.backend.repository.EmployeeRepository;
import Israt.example.Hr.and.payroll.management.system.backend.repository.LeaveRequestRepository;
import Israt.example.Hr.and.payroll.management.system.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class LeaveRequestService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public LeaveRequest createLeaveRequest(LeaveRequest leaveRequest) {
        return leaveRequestRepository.save(leaveRequest);
    }

    public LeaveRequest getLeaveRequestById(Long leaveRequestId) {
        Optional<LeaveRequest> leaveRequest = leaveRequestRepository.findById(leaveRequestId);
        return leaveRequest.orElse(null);
    }

    public List<LeaveRequest> getLeaveRequestsByEmployeeId(Long employeeId) {
        return leaveRequestRepository.findByEmployeeId(employeeId);
    }

    public List<LeaveRequest> getLeaveRequestsByStatus(String status) {
        return leaveRequestRepository.findByStatus(status);
    }

    public List<LeaveRequest> getLeaveRequestsByEmployeeIdAndStatus(Long employeeId, String status) {
        return leaveRequestRepository.findByEmployeeIdAndStatus(employeeId, status);
    }

    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestRepository.findAll();
    }

    public LeaveRequest updateLeaveRequest(Long leaveRequestId, LeaveRequest leaveRequestDetails) {
        Optional<LeaveRequest> leaveRequest = leaveRequestRepository.findById(leaveRequestId);
        if (leaveRequest.isPresent()) {
            LeaveRequest lr = leaveRequest.get();
            lr.setLeaveType(leaveRequestDetails.getLeaveType());
            lr.setStartDate(leaveRequestDetails.getStartDate());
            lr.setEndDate(leaveRequestDetails.getEndDate());
            lr.setTotalDays(leaveRequestDetails.getTotalDays());
            lr.setReason(leaveRequestDetails.getReason());
            lr.setContactNumber(leaveRequestDetails.getContactNumber());
            lr.setHandoverNotes(leaveRequestDetails.getHandoverNotes());
            lr.setStatus(leaveRequestDetails.getStatus());
            lr.setApprovalNotes(leaveRequestDetails.getApprovalNotes());
            lr.setAttachments(leaveRequestDetails.getAttachments());
            return leaveRequestRepository.save(lr);
        }
        return null;
    }

    public LeaveRequest approveLeaveRequest(Long leaveRequestId, LeaveRequest approvalDetails) {
        Optional<LeaveRequest> leaveRequest = leaveRequestRepository.findById(leaveRequestId);
        if (leaveRequest.isPresent()) {
            LeaveRequest lr = leaveRequest.get();
            lr.setStatus("Approved");
            lr.setApprovedBy(approvalDetails.getApprovedBy());
            lr.setApprovalDate(approvalDetails.getApprovalDate());
            lr.setApprovalNotes(approvalDetails.getApprovalNotes());
            return leaveRequestRepository.save(lr);
        }
        return null;
    }

    public LeaveRequest rejectLeaveRequest(Long leaveRequestId, LeaveRequest rejectionDetails) {
        Optional<LeaveRequest> leaveRequest = leaveRequestRepository.findById(leaveRequestId);
        if (leaveRequest.isPresent()) {
            LeaveRequest lr = leaveRequest.get();
            lr.setStatus("Rejected");
            lr.setRejectedBy(rejectionDetails.getRejectedBy());
            lr.setRejectionReason(rejectionDetails.getRejectionReason());
            return leaveRequestRepository.save(lr);
        }
        return null;
    }

    public void deleteLeaveRequest(Long leaveRequestId) {
        leaveRequestRepository.deleteById(leaveRequestId);
    }

    public List<LeaveRequest> getPendingLeaveRequests() {
        return leaveRequestRepository.findByStatus("Pending");
    }

    public List<LeaveRequest> getApprovedLeaveRequests() {
        return leaveRequestRepository.findByStatus("Approved");
    }

    public List<LeaveRequest> getRejectedLeaveRequests() {
        return leaveRequestRepository.findByStatus("Rejected");
    }

    public boolean isOwnerOfLeaveRequest(Long leaveRequestId, String userEmail) {
        Optional<LeaveRequest> leaveRequest = leaveRequestRepository.findById(leaveRequestId);
        if (leaveRequest.isPresent()) {
            Long employeeId = leaveRequest.get().getEmployeeId();
            Optional<Employee> employee = employeeRepository.findById(employeeId);
            if (employee.isPresent()) {
                return employee.get().getEmail().equals(userEmail);
            }
        }
        return false;
    }

    public boolean isUserOwnerOfEmployeeLeaveRequests(Long employeeId, String userEmail) {
        Optional<Employee> employee = employeeRepository.findById(employeeId);
        if (employee.isPresent()) {
            return employee.get().getEmail().equals(userEmail);
        }
        return false;
    }

    public boolean isUserAdmin(String userEmail) {
        Optional<User> user = userRepository.findByEmail(userEmail);
        if (user.isPresent() && user.get().getRole() == UserRole.ADMIN) return true;
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            return auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMIN"));
        }
        return false;
    }
}
