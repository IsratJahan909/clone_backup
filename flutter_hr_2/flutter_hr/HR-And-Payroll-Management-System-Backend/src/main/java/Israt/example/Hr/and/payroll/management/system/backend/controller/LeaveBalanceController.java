package Israt.example.Hr.and.payroll.management.system.backend.controller;

import Israt.example.Hr.and.payroll.management.system.backend.entity.LeaveBalance;
import Israt.example.Hr.and.payroll.management.system.backend.service.LeaveBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/leaveBalances")
@CrossOrigin(origins = "*")
public class LeaveBalanceController {

    @Autowired
    private LeaveBalanceService leaveBalanceService;

    @PostMapping
    public ResponseEntity<?> createLeaveBalance(@RequestBody LeaveBalance leaveBalance) {
        try {
            LeaveBalance newLeaveBalance = leaveBalanceService.createLeaveBalance(leaveBalance);
            return ResponseEntity.status(HttpStatus.CREATED).body(newLeaveBalance);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error creating leave balance: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLeaveBalanceById(@PathVariable Long id) {
        try {
            LeaveBalance leaveBalance = leaveBalanceService.getLeaveBalanceById(id);
            if (leaveBalance != null) {
                return ResponseEntity.ok(leaveBalance);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Leave balance not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching leave balance");
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllLeaveBalances() {
        try {
            List<LeaveBalance> leaveBalances = leaveBalanceService.getAllLeaveBalances();
            return ResponseEntity.ok(leaveBalances);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching leave balances");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLeaveBalance(@PathVariable Long id, @RequestBody LeaveBalance leaveBalance) {
        try {
            LeaveBalance updatedLeaveBalance = leaveBalanceService.updateLeaveBalance(id, leaveBalance);
            if (updatedLeaveBalance != null) {
                return ResponseEntity.ok(updatedLeaveBalance);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Leave balance not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating leave balance");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLeaveBalance(@PathVariable Long id) {
        try {
            leaveBalanceService.deleteLeaveBalance(id);
            return ResponseEntity.ok("Leave balance deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting leave balance");
        }
    }
}
