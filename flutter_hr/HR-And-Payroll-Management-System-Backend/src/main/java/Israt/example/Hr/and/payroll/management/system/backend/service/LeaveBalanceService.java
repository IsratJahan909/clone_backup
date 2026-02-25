package Israt.example.Hr.and.payroll.management.system.backend.service;

import Israt.example.Hr.and.payroll.management.system.backend.entity.LeaveBalance;
import Israt.example.Hr.and.payroll.management.system.backend.repository.LeaveBalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class LeaveBalanceService {

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    public LeaveBalance createLeaveBalance(LeaveBalance leaveBalance) {
        return leaveBalanceRepository.save(leaveBalance);
    }

    public LeaveBalance getLeaveBalanceById(Long id) {
        Optional<LeaveBalance> leaveBalance = leaveBalanceRepository.findById(id);
        return leaveBalance.orElse(null);
    }

    public List<LeaveBalance> getAllLeaveBalances() {
        return leaveBalanceRepository.findAll();
    }

    public List<LeaveBalance> getLeaveBalancesByUserId(Long userId) {
        return leaveBalanceRepository.findByUserId(userId);
    }

    public List<LeaveBalance> getLeaveBalancesByEmployeeId(Long employeeId) {
        return leaveBalanceRepository.findByEmployeeId(employeeId);
    }

    public LeaveBalance updateLeaveBalance(Long id, LeaveBalance leaveBalanceDetails) {
        Optional<LeaveBalance> leaveBalance = leaveBalanceRepository.findById(id);
        if (leaveBalance.isPresent()) {
            LeaveBalance lb = leaveBalance.get();
            lb.setUserId(leaveBalanceDetails.getUserId());
            lb.setEmployeeId(leaveBalanceDetails.getEmployeeId());
            lb.setTotalLeaves(leaveBalanceDetails.getTotalLeaves());
            lb.setUsedLeaves(leaveBalanceDetails.getUsedLeaves());
            lb.setRemainingLeaves(leaveBalanceDetails.getRemainingLeaves());
            lb.setSickLeaves(leaveBalanceDetails.getSickLeaves());
            lb.setCasualLeaves(leaveBalanceDetails.getCasualLeaves());
            lb.setEarnedLeaves(leaveBalanceDetails.getEarnedLeaves());
            return leaveBalanceRepository.save(lb);
        }
        return null;
    }

    public void deleteLeaveBalance(Long id) {
        leaveBalanceRepository.deleteById(id);
    }
}